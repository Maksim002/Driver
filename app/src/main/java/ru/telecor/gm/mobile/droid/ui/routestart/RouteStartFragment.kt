package ru.telecor.gm.mobile.droid.ui.routestart

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.fragment_route_start.*
import kotlinx.android.synthetic.main.fragment_route_start.image_profile
import kotlinx.android.synthetic.main.fragment_route_start.pbLoading
import kotlinx.android.synthetic.main.fragment_route_start.tvDriverName
import kotlinx.android.synthetic.main.item_base_tool_bar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.Screens
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.extensions.color
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.routestart.RouteStartPresenter
import ru.telecor.gm.mobile.droid.presentation.routestart.RouteStartView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.NewBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestart.fragment.message.MessageConfirmationSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestart.rv.RouteInfoAdapter
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.terrakok.cicerone.Router
import timber.log.Timber
import toothpick.Toothpick
import java.util.*

class RouteStartFragment : BaseFragment(), RouteStartView {
    private lateinit var adapters: RouteInfoAdapter

    @InjectPresenter
    lateinit var presenter: RouteStartPresenter

    @ProvidePresenter
    fun providePresenter(): RouteStartPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(RouteStartPresenter::class.java)
    }

    companion object {
        fun newInstance() = RouteStartFragment()
    }

    override val layoutRes = R.layout.fragment_route_start

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        initClick()
        alertDialog.show()
        adapters =  RouteInfoAdapter(object : RouteInfoAdapter.Listener{
            override fun clickLambdaListener(item: RouteInfo) {
                presenter.routeClicked(item, context)
            }
        },this, presenter)

        rvRoutes.layoutManager = LinearLayoutManager(context)
        rvRoutes.adapter = adapters
    }

    private fun initClick() {
        btnSettings.setOnClickListener { showSettingsMenu() }
        imgInternetIndicator.setOnClickListener {
            if (!layoutComponents.isVisible){
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.colorAccent))
                layoutComponents.isVisible = true
            }else{
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
                layoutComponents.isVisible = false
            }
        }
    }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value, View.INVISIBLE)
    }

    override fun setAlert(value: Boolean) {
        if (value) alertDialog.show() else alertDialog.hide()
    }

    override fun setRoutesList(list: List<RouteInfo>) {
        adapters.update(list)
    }

    override fun setAlertDialog(router: Router) {
        if (presenter.displayingWindow){
            if(!presenter.displayingMessage){
                try {
                    val bottomSheetDialogFragment = MessageConfirmationSheetFragment(
                        object : MessageConfirmationSheetFragment.Listener{
                            override fun setOnClickListener() {
                                router.newRootScreen(Screens.Task())
                            }
                        })
                    bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }else{
                router.newRootScreen(Screens.Task())
            }
        }
        presenter.displayingWindow = false
    }

    @SuppressLint("SetTextI18n")
    override fun setDriver(driver: DriverInfo) {
        tvDriverName.text =
            "${driver.lastName.emptyIfNull()} ${driver.firstName.emptyIfNull()} ${driver.middleName.emptyIfNull()}"
        nameTxt.text = "Удачной смены, ${driver.firstName.emptyIfNull()} ${driver.middleName.emptyIfNull()}!"
    }

    override fun sendLoginRequestAndUpdate() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener {
            if (it.isSuccessful) {
                val loginData =
                    getLoginData(it.result.also { fid -> Timber.w("FID is set: $fid") }
                        ?: "".also {
                            showMessage("FID не установлен")
                            Timber.e("FID is null")
                        }
                    )
                presenter.updateData(loginData)
            } else {
                Timber.e("Unable to get FID")
                showMessage("Не удалось войти")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLoginData(id: String): LoginRequest {
        var gpsTime: Long = 0
        var lat: Double = 0.0
        var lon: Double = 0.0
        val time: Long = Date().time
        var imei = ""

        // get location info
        val l = LocationUtils.getBestLocation(requireContext())
        if (l != null) {
            gpsTime = l.time
            lon = l.longitude
            lat = l.latitude
        }

        imei = id

        return LoginRequest(
            gpsTime, imei, "", lat, lon, time,
            VersionData(2, 0, 135)
        )
    }

    override fun showUpdateDialog(version: BuildVersion) {
        NewBottomSheetFragment.newInstance(version.serverName, null, "Обновить", true)
            .show(requireActivity().supportFragmentManager, "newBottomSheetFragment")
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun setImProfile(boolean: Boolean, stream: Bitmap?) {
        if (boolean && stream != null){
            image_profile.setImageBitmap(stream)
        }else{
            image_profile.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile, null))
        }
    }

    fun showSettingsMenu() {
        presenter.isVisibilityNext(View.VISIBLE)
        val bottomSheetDialogFragment = SettingBottomSheetFragment()
        bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun setFirstLoaderList(list: List<LoaderInfo>) {
        adapters.getListDown(list)
        alertDialog.hide()
    }

    override fun setSecondLoaderList(list: List<LoaderInfo>) {
        adapters.getListDown(list)
        alertDialog.hide()
    }

    override fun setFirstLoaderPreselected(loaderInfo: LoaderInfo) {
        adapters.setFirstLoaderPreselected(loaderInfo)
    }

    override fun setSecondLoaderPreselected(loaderInfo: LoaderInfo) {
        adapters.setSecondLoaderPreselected(loaderInfo)
    }

    override fun setRouteInfo(routeInfo: RouteInfo) {
        // TODO: 01.04.2022  Сюда приходит информация маршрута в будущем она понадобится
    }

    override fun showCurrentTime(string: String) {
        tvClock.text = string
    }

    override fun showBatteryLevel(string: String) {
        tvBattery.text = string
    }

    override fun showLackInternet(error: String) {
        alertDialog.hide()
    }

    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            presenter.updateBatteryLevel(level.toString() + "%")
        }
    }

    //I came to this decision because the structure of the application does not work correctly.
    //It is dangerous to rewrite it. Therefore, this decision is the most relevant.
    //No time was allocated for the decision.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutCon.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gmm_white
                ))
            tvDriverName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_text_w))
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutCon.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                ))
            tvDriverName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gmm_white))
        }
    }
}
