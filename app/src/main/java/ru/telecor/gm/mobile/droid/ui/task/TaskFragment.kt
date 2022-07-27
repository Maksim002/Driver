package ru.telecor.gm.mobile.droid.ui.task

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import com.nhaarman.supertooltips.ToolTip
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.fragment_task.imgInternetIndicator
import kotlinx.android.synthetic.main.fragment_task.layoutComponents
import kotlinx.android.synthetic.main.fragment_task.tvPreferredTime
import kotlinx.android.synthetic.main.item_base_tool_bar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.json.JSONObject
import ru.telecor.gm.mobile.droid.AppConstants
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.utils.SecurityUtils
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.extensions.color
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.task.TaskPresenter
import ru.telecor.gm.mobile.droid.presentation.task.TaskView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.exitDialog.ExitDialogFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.map.MapActivity
import ru.telecor.gm.mobile.droid.ui.task.fragment.GettingStartedFragment
import ru.telecor.gm.mobile.droid.ui.task.rv.CansGarbageTaskAdapter
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils.isTrafficInternet
import toothpick.Toothpick


class TaskFragment : BaseFragment(), TaskView {

    companion object {

        fun newInstance() = TaskFragment()

        fun newInstance(taskId: Int) = TaskFragment().apply {
            arguments = Bundle().apply {
                putInt(TASK_ID_KEY, taskId)
            }
        }
        private const val TASK_ID_KEY = "taskid"
    }

    override val layoutRes = R.layout.fragment_task

    @InjectPresenter
    lateinit var presenter: TaskPresenter

    @ProvidePresenter
    fun providePresenter(): TaskPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(TaskPresenter::class.java)
            .apply { taskId = arguments?.getInt(TASK_ID_KEY) ?: -1 }
    }

    private var recyclerAdapter = CansGarbageTaskAdapter()

    private lateinit var loadingDialog: AlertDialog

    private var googleMap: GoogleMap? = null

    private var valueClick: Boolean = false

    private var addressTxt = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertDialog.hide()
        initBuildingChronology()
        requireActivity().registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        btnToDiscovery.setOnClickListener {
            presenter.cameButtonClicked()
        }

        btnSetAsCurrent.setOnClickListener {
            btnSetAsCurrent.isClickable = false
            presenter.setAsCurrentButtonClicked()
        }

        //Блок отоброжает параметры телефона
        imgInternetIndicator.setOnClickListener {
            if (!layoutComponents.isVisible){
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.colorAccent))
                layoutComponents.isVisible = true
                tvAddress.isVisible = false
            }else{
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
                layoutComponents.isVisible = false
                tvAddress.isVisible = true
            }
        }

        btnSettings.setOnClickListener {
            valueClick = true
            presenter.onSettingsClicked()
        }

        btnBack.setOnClickListener {
//            presenter.onBackPressed()
            presenter.routeButtonClicked()

        }

        btnNavigator.setOnClickListener {
            presenter.navigatorButtonClicked()
        }

        //при клике переводит номер в набор номера.
        tvContactsLabel.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${tvContactsLabel.text}")
            startActivity(intent)
        }

        //Всплывающее сообщение с адресом
        tvAddress.setOnClickListener {
            if (addressTxt != ""){
                val toolTip = ToolTip()
                    .withText(addressTxt)
                    .withColor(resources.getColor(R.color.gray2))

                val message = addressMessage.showToolTipForView(toolTip,addressMessage)
                Handler().postDelayed({
                    message.remove()
                }, 2500)
            }else if (addressTxt == ""){
                showMessage("Адрес пусть")
            }
        }

        // recycler view initialization
        rvContainers.adapter = recyclerAdapter

        val inflatedView = LayoutInflater.from(context)
            .inflate(R.layout.progress_bar_dialog, view as ViewGroup, false)

        loadingDialog = MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.task_fragment_loading_dialog_title)
            .setView(inflatedView)
            .setCancelable(false)
            .create()

        gMap.onCreate(Bundle())
        gMap.getMapAsync {
            googleMap = it
            presenter.onMapReady()
        }
    }

    private fun initBuildingChronology() {
        layoutTask.setOnClickListener {
            if (containerTask.isVisible) {
                taskDetailsIm.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_drop_down_24
                    )
                )
                containerTask.isVisible = false
            } else {
                taskDetailsIm.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_drop_up_24
                    )
                )
                containerTask.isVisible = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        gMap.onStart()
    }

    override fun onStop() {
        gMap.onStop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        gMap.onResume()
    }

    override fun onPause() {
        gMap.onPause()

        super.onPause()
    }

    override fun startNavigationApplication(lat: Double, lon: Double) {
        if (isSupportedYandexNavi()) {
            var uri = Uri.parse("yandexnavi://build_route_on_map").buildUpon()
                .appendQueryParameter("lat_to", lat.toString())
                .appendQueryParameter("lon_to", lon.toString())
                .appendQueryParameter("client", AppConstants.YANDEX_CLIENT)
                .build()

            uri = uri.buildUpon()
                .appendQueryParameter(
                    "signature", SecurityUtils.sha256rsa(
                        AppConstants.YANDEX_PRIVATE_KEY, uri.toString()
                    )
                )
                .build()

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            val uri = "geo:$lat,$lon?q=$lat,$lon"
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(mapIntent)
        }
    }

    override fun setRouteNumber(str: String) {
//        tvRouteNumber.text = str
    }

    @SuppressLint("SetTextI18n")
    override fun setLoadLevel(str: String) {
//        tvLoadLevel.text =
//            "${getString(R.string.task_fragment_load_level_label_text)} ${str.emptyIfNull()}"
    }

    override fun setAddress(str: String) {
        addressTxt = str
        tvAddress.text = str
    }

    override fun setCustomerInfo(str: String) {
        val text = str.replace("[", "")
        tvCustomer.text = text.replace("]", "")
    }

    override fun setContactNumber(str: String) {
        iconTorso.visible(str.isNotEmpty())
        tvContactsLabel.text = str.emptyIfNull()
//        tvContacts.visible(str.isNotEmpty())
//        tvContacts.text = str.emptyIfNull()
    }

    override fun setPreferredTime(str: String) {
        tvPreferredTime.text = str
    }

    override fun setPlanTimeSten(str: String) {
        plannedTxt.text = str
    }

    override fun setComment(str: String) {
//        tvCommentLabel.visible(str.isNotEmpty())
//        tvComment.visible(str.isNotEmpty())
//        tvComment.text = str
    }

    override fun setPriority(str: String) {
//        tvPriorityLabel.visible(str.isNotEmpty())
        iconTextMessage.visible(str.isNotEmpty())
        tvPriority.text = str
    }

    override fun setRouteComment(str: String) {
        layoutIcon.visible(str.isNotEmpty())
        routeComment.text = str
    }

    override fun setTaskList(taskItemPreviewData: List<TaskItemPreviewData>) {
        recyclerAdapter.update(taskItemPreviewData)
    }

    //Открытие диалогового окна для заполнения маршрута
    override fun showLoadDialog(standName: String) {
        val bottomSheetDialogFragment = GettingStartedFragment(
            object : GettingStartedFragment.Listener{
                override fun setOnClickBeginningListener() {
                    presenter.onDialogConfirmed()
                }
                override fun setOnClickRouteListener() {
                    presenter.onDialogProblemSelected()
                }
            }, standName)
        bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)

//        if (!cameDialog.isShowing) {
//            cameDialog.setMessage(standName)
//            cameDialog.show()
//        }
    }

    override fun setLoadingState(value: Boolean) {
        if (value)
            alertDialog.show()
        else
            alertDialog.hide()
    }

    override fun setLastTaskMode(value: Boolean) {
//        tvFinalTaskInfo.visible(value)
        rvContainers.visible(!value)
    }

    override fun showFinishDialog() {
        try {
            if (activity != null && context != null) {
                isStartConnected()
            } else {
                Screens.Task()
                isStartConnected()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isStartConnected() {
        CoroutineScope(Dispatchers.IO).launch {
            if (ConnectivityUtils.isConnected(requireContext()) && isTrafficInternet()) {
                requireActivity().runOnUiThread {
                    fragmentManager?.let {
                        ExitDialogFragment().show(it, "finishDialog")
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    getLackInternet()
                }
            }
        }
    }

    override fun taskShowMode(mode: TaskView.Mode) {
        when (mode) {
            TaskView.Mode.CURRENT -> {
                btnSetAsCurrent.visible(false)
                btnToDiscovery.visible(true)
            }
            TaskView.Mode.PREVIEW -> {
                btnSetAsCurrent.visible(true)
                btnToDiscovery.visible(false)
            }
            TaskView.Mode.DONE -> {
                btnSetAsCurrent.visible(false)
                btnToDiscovery.visible(false)
            }
        }
    }

    override fun showPolygonRequestLoadingDialog(value: Boolean) {
//        try {
//            if (value) {
//                loadingDialog.show()
//            } else {
//                loadingDialog.dismiss()
//            }
//        }catch (e: Exception){
//            loadingDialog.dismiss()
//        }
    }

    private fun getLackInternet() {
        try {
            if (activity != null && context != null) {
                isStartDialog()
            } else {
                Screens.Task()
                isStartDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setClickable(clickable: Boolean){
        //btnToDiscovery.isEnabled = false
    }

    private fun isStartDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Пожалуйста проверьте подключение к интернету.")
            .setCancelable(false)
            .setPositiveButton("Повторить") { dialog, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (isTrafficInternet()) {
                        requireActivity().runOnUiThread {
                            presenter.routeButtonClicked()
                        }
                    } else {
                        presenter.cameButtonClicked()
                    }
                    dialog.dismiss()
                }
            }
            .show()
    }

    override fun showPolygonRequestRetryDialog() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.task_fragment_polygon_request_dialog_title)
            .setMessage(R.string.task_fragment_polygon_request_dialog_message)
            .setCancelable(false)
            .setPositiveButton(R.string.task_fragment_polygon_request_dialog_positive) { dialog, _ ->
                run {
                    presenter.askThePolygonDestination()
                    dialog.dismiss()
                }
            }
            .show()
    }

    override fun showInternedNeeded() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(R.string.error_connection_failed)
            .setMessage(R.string.task_fragment_polygon_request_dialog_message)
            .setOnCancelListener { presenter.onPolygonDestinationDialogCancelled() }
            .setNegativeButton("Отмена") { l, _ ->
                l.dismiss()
                presenter.onPolygonDestinationDialogCancelled()
            }
            .setPositiveButton(R.string.task_fragment_polygon_request_dialog_positive) { dialog, _ ->
                run {
                    presenter.askThePolygonDestination()
                    dialog.dismiss()
                }
            }
            .show()
    }


    override fun showPolygonSelectionDialog(variantsList: List<VisitPoint>) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.task_fragment_select_polygon_dialog_title))
            .setItems(
                variantsList.map { item -> item.name }.toTypedArray()
            ) { _, position ->
                presenter.onVisitPointSelected(variantsList[position])
            }
            .setOnCancelListener { presenter.onPolygonDestinationDialogCancelled() }
            .setNegativeButton("Отмена") { l, _ ->
                l.dismiss()
                presenter.onPolygonDestinationDialogCancelled()
            }
            .show()

    }

    override fun showPolygonSelectionEnsureDialog(selected: VisitPoint) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Площадка \"${selected.name}\". Вы уверены?")
            .setPositiveButton("Да") { _, _ -> presenter.onVisitPointEnsured(selected) }
            .setNegativeButton("Нет") { di, _ -> di.dismiss() }
            .setOnCancelListener { presenter.onVisitPointEnsureCancelled() }
            .show()
    }

    override fun showCurrentTime(string: String) {
        tvClock.text = string
    }

    override fun showBatteryLevel(string: String) {
        tvBattery.text = string
    }

    override fun showSettingsMenu() {
        presenter.isVisibilityNext(View.VISIBLE)
        if (valueClick){
            val bottomSheetDialogFragment = SettingBottomSheetFragment()
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
            valueClick = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun showMapLocation(
        lat: Double,
        lon: Double,
        standGeoJson: String?,
        evacuationGeoJson: String?,
        routeToRoad: String?
    ) {
        googleMap?.let {
            it.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker"))
            it.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(lat, lon))
                        .zoom(16f)
                        .build()
                )
            )

            withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,
                {
                    withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        {
                            withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                {
                                    it.isMyLocationEnabled = true
                                    it.uiSettings?.isMyLocationButtonEnabled = false
                                },
                                {})
                        },
                        {})
                },
                {})

            val redLineStyle = GeoJsonLineStringStyle()
            redLineStyle.color = Color.RED

            val greenLineStyle = GeoJsonLineStringStyle()
            greenLineStyle.color = Color.GREEN

            val blueLineStyle = GeoJsonLineStringStyle()
            blueLineStyle.color = Color.BLUE

            if (standGeoJson != null && evacuationGeoJson != null) {
                val standObj = JSONObject(standGeoJson)
                val standLayer = GeoJsonLayer(it, standObj)
                standLayer.features.forEach { item ->
                    item.lineStringStyle = redLineStyle
                    item.polygonStyle = GeoJsonPolygonStyle().apply { strokeColor = Color.RED }
                }

                val evacuationObj = JSONObject(evacuationGeoJson)
                val evacuationLayer = GeoJsonLayer(it, evacuationObj)
                evacuationLayer.features.forEach { item -> item.lineStringStyle = greenLineStyle }

                standLayer.addLayerToMap()
                evacuationLayer.addLayerToMap()
            }

            if (routeToRoad != null) {
                val obj = JSONObject(routeToRoad)
                val layer = GeoJsonLayer(it, obj)
                layer.features.forEach { item -> item.lineStringStyle = blueLineStyle }
                layer.addLayerToMap()
            }
        }
    }

    override fun openFullScreenMap(
        lat: Double,
        lon: Double,
        standGeoJson: String?,
        evacuationGeoJson: String?,
        routeToRoad: String?
    ) {
        startActivity(
            MapActivity.createIntent(
                standGeoJson,
                evacuationGeoJson,
                routeToRoad,
                lat,
                lon,
                requireContext()
            )
        )
    }

    private fun isSupportedYandexNavi(): Boolean {
        val intent = Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP")
        intent.setPackage(AppConstants.YANDEX_NAVI_PACKAGE)
        val pm: PackageManager? = activity?.packageManager
        val activities = pm?.queryIntentActivities(intent, 0)
        return !activities.isNullOrEmpty()
    }

    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            presenter.updateBatteryLevel(level.toString() + "%")
        }
    }
}
