package ru.telecor.gm.mobile.droid.ui.dumping

import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.edit_text_dialog.view.*
import kotlinx.android.synthetic.main.fragment_dumping.*
import kotlinx.android.synthetic.main.item_base_tool_bar.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.AppConstants
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.CompletedContainerInfo
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.extensions.color
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.dumping.DumpingPresenter
import ru.telecor.gm.mobile.droid.presentation.dumping.DumpingView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.dumping.fragment.authorization.AuthorizationBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.dumping.fragment.successful.SuccessfulReceiptSheetFragment
import ru.telecor.gm.mobile.droid.ui.dumping.fragment.unloading.CancellationUnloadingSheetFragment
import ru.telecor.gm.mobile.droid.ui.dumping.rv.CompletedContainersAdapter
import ru.telecor.gm.mobile.droid.ui.dumping.rv.DumpingAdapter
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.utils.SecurityUtils
import toothpick.Toothpick
import androidx.core.content.ContextCompat
import android.widget.LinearLayout

class DumpingFragment : BaseFragment(), DumpingView {

    override val layoutRes = R.layout.fragment_dumping

    @InjectPresenter
    lateinit var presenter: DumpingPresenter

    @ProvidePresenter
    fun providePresenter(): DumpingPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(DumpingPresenter::class.java)
    }

    companion object {

        fun newInstance() = DumpingFragment()
    }

    private var recyclerAdapter = CompletedContainersAdapter()
    private var getStringWeight = 0

    private val adapters = DumpingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().registerReceiver(
            batteryBroadcastReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        presenter.restartWeights()
        initClick()
        rvDumpingContainers.layoutManager = LinearLayoutManager(requireContext())
        rvDumpingContainers.adapter = recyclerAdapter
    }

    private fun initClick() {
        btnCame.setOnClickListener {
            val bottomSheetDialogFragment = AuthorizationBottomSheetFragment()
            bottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag
            )
        }
        btnNavigator.setOnClickListener { presenter.onNavigatorButtonClicked() }
        btnToRoute.setOnClickListener {
            //Если есть первый и второй вес
            val bottomSheetDialogFragment = CancellationUnloadingSheetFragment(
                object : CancellationUnloadingSheetFragment.UnloadingListener {
                    override fun setOnClickListener() {
                        presenter.isSavingTicket.value = "false"
                        presenter.isTicketNumber.value = 0
                        presenter.onRouteButtonClicked()
                    }
                })
            bottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag
            )
        }

        btnSettings.setOnClickListener { showSettingsMenu() }
        imgInternetIndicator.setOnClickListener {
            if (!layoutComponents.isVisible) {
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.colorAccent))
                layoutComponents.isVisible = true
            } else {
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
                layoutComponents.isVisible = false
            }
        }

        repeatingWeightRequest.setOnClickListener {
            presenter.getFlightTicketModel()
        }
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

    override fun showInternetConnection(connectionAvailable: Boolean) {
        if (connectionAvailable) {
            imgInternetIndicator.setImageResource(R.drawable.ic_antenna_max)
        } else {
            imgInternetIndicator.setImageResource(R.drawable.ic_antenna_min)
        }
    }

    //Получение талонов
    override fun showListCoupons(list: GetListCouponsModel) {
        val item: ArrayList<GetListCouponsModel> = arrayListOf()
        if (presenter.isSavingTicket.value == "true") {
            if (list.recycling != null) {
                tvAddressLabel.text = list.recycling!!.name
            }
            when (list.recycling!!.weigherType) {
                "LONG" -> {
                    weigherDeviceValueTxt.text = resources.getString(R.string.text_short_long)
                }
                "NONE" -> {
                    weigherDeviceValueTxt.text = ""
                    imageScales.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_not_required,
                            null
                        )
                    )
                    textScales.text = resources.getString(R.string.missing_weights)
                }
                else -> {
                    weigherDeviceValueTxt.text = resources.getString(R.string.text_short)
                }
            }
            if (list.unloading!!.size != 0) {
                list.unloading!!.forEach { up ->
                    //Если талон найден
                    if (up.visitPoint!!.equipmentStatus != "NOT_EQUIPED") {
                        dumpingRecyclerView.isVisible = true
                        layoutNotRequired.isVisible = false
                        notRequiredMessageTxt.isVisible = false
                        layTicketNotFound.isVisible = false
                        btnCame.isVisible = false
                        continuationBtn.isVisible = true

                        //Если получен первый вес
                        if (up.primaryWeight != null) {
                            newMessageTxt.isVisible = true
                            usedMessageTxt.isVisible = false
                        } else {
                            newMessageTxt.isVisible = false
                            usedMessageTxt.isVisible = true
                        }

                        //Если получен второй вес
                        btnToRoute.isClickable = up.secondaryWeight == null

                        // Если нет веса запускаем таймер
                        if (up.netWeight != null) {
                            getStringWeight = up.netWeight!!.toInt()
                            finalWeightText.text = "$getStringWeight кг"
//                            presenter.timeStop()
                        } else {
//                            presenter.timeStart()
                        }
                    } else {
                        //Если талон не требуется
                        ticketNotRequired()
                    }
                    //Кнопка продолжить
                    continuationBtn.setOnClickListener {
                        continueRoute(up.netWeight.toString())
                    }
                }
            } else {
                //Если талон не найден
                ticketNotFound()
            }

            if (presenter.isSavingTicket.value == "true") {
                //Заполненеие адаптера если талоны есть
                if (list.unloading?.size != 0) {
                    item.add(list)
                    adapters.update(item)
                } else {
                    //Если талон не найден
                    ticketNotFound()
                }
            }
        }
        dumpingRecyclerView.adapter = adapters
    }

    //Если талон не требуется
    private fun ticketNotRequired() {
        textTalonLine.text = "Талон не требуется"
        continuationBtn.isVisible = false
        layoutNotRequired.isVisible = true
        notRequiredMessageTxt.isVisible = false
        layTicketNotFound.isVisible = false
        btnCame.isVisible = true
    }

    //Если талон не найден
    private fun ticketNotFound() {
        textTalonLine.text = "Талон не найден"
        layoutNotRequired.isVisible = true
        layTicketNotFound.isVisible = true
        dumpingRecyclerView.isVisible = false
        notRequiredMessageTxt.isVisible = false
    }

    fun showSettingsMenu() {
        presenter.isVisibilityNext(View.VISIBLE)
        val bottomSheetDialogFragment = SettingBottomSheetFragment()
        bottomSheetDialogFragment.show(
            requireActivity().supportFragmentManager,
            bottomSheetDialogFragment.tag
        )
    }

    override fun setAddress(string: String) {
        tvAddress.text = string
    }

    override fun setContainersList(list: List<CompletedContainerInfo>) {
        recyclerAdapter.setList(list)
        tvContainersTotalVolume.text =
            list.map { container -> container.volume }.sum().toString()
    }

    override fun setExpectedVolume(value: Double) {
        tvExpectedVolume.text = "Фактический объем: $value м³"
    }

    override fun showWeightDialog() {
        presenter.initToClick.value = false
        val inflatedView = LayoutInflater.from(context)
            .inflate(R.layout.edit_text_dialog, view as ViewGroup, false)
        MaterialAlertDialogBuilder(context!!)
            .setCancelable(false)
            .setTitle(getString(R.string.dumping_dialog_title))
            .setView(inflatedView)
            .setPositiveButton(getString(R.string.dumping_dialog_positive)) { _: DialogInterface, _: Int ->
                presenter.initToClick.value = true
                presenter.onPolygonWeightEntered(inflatedView.tietWeight.text.toString())
            }
            .setNegativeButton(getString(R.string.dumping_dialog_negative)) { dialogInterface: DialogInterface?, _: Int ->
                dialogInterface?.dismiss()
                presenter.initToClick.value = true
            }
            .show()
    }

    override fun setLoadingState(value: Boolean) {
        if (value) alertDialog.show() else alertDialog.hide()
    }

    override fun showDoingBack() {}

    // По истечению ыремени закрытие маршрута
    override fun showRouteClosure(number: Int) {
        continueRoute(number.toString())
    }

    private fun continueRoute(number: String) {
        if (number != "null" && number != "99999") {
            // Остоновка таймера
//            presenter.timeStop()
            // Отправка веса
            presenter.onPolygonWeightEntered(number)
            presenter.isSavingTicket.value = "false"
            presenter.isTicketNumber.value = 0
        } else {
            val bottomSheetDialogFragment = SuccessfulReceiptSheetFragment(
                object : SuccessfulReceiptSheetFragment.ListenerSuccessfulReceipt {
                    override fun setonClickListener() {
                        // Остоновка таймера
//                        presenter.timeStop()
                        // Отправка веса
                        presenter.onPolygonWeightEntered("1")
                        if (number != "null") {
                            presenter.onPolygonWeightEntered(number)
                            presenter.isSavingTicket.value = "false"
                            presenter.isTicketNumber.value = 0
                        } else {
                            presenter.onPolygonWeightEntered("1")
                        }
                    }
                })
            bottomSheetDialogFragment.isCancelable = false
            bottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag
            )
        }
    }

    override fun showCurrentTime(string: String) {
        tvClock.text = string
    }

    override fun showBatteryLevel(string: String) {
        tvBattery.text = string
    }

    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            presenter.updateBatteryLevel(level.toString() + "%")
        }
    }

    private fun isSupportedYandexNavi(): Boolean {
        val intent = Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP")
        intent.setPackage(AppConstants.YANDEX_NAVI_PACKAGE)
        val pm: PackageManager? = activity?.packageManager
        val activities = pm?.queryIntentActivities(intent, 0)
        return !activities.isNullOrEmpty()
    }

    //I came to this decision because the structure of the application does not work correctly.
    //It is dangerous to rewrite it. Therefore, this decision is the most relevant.
    //No time was allocated for the decision.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        val myLayoutRecycler: LinearLayout.LayoutParams =
            layoutRecycler.layoutParams as LinearLayout.LayoutParams
        val myLayoutText: LinearLayout.LayoutParams =
            layoutText.layoutParams as LinearLayout.LayoutParams
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutFund.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gmm_white
                )
            )
            fillingResources(R.color.color_text, R.color.gmm_white)
            myLayoutRecycler.setMargins(8, 60, 8, 0)
            myLayoutText.setMargins(10, 10, 10, 0)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutFund.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                )
            )
            fillingResources(R.color.gmm_white, R.color.color_text)
            myLayoutRecycler.setMargins(8, 170, 8, 0)
            myLayoutText.setMargins(10, 25, 10, 0)
        }
    }

    private fun fillingResources(color: Int, colorCon: Int) {
        finalWeightText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
        weigherDeviceValueTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
        textScales.setTextColor(ContextCompat.getColor(requireContext(), color))
        imageScales.setColorFilter(
            ContextCompat.getColor(requireContext(), color),
            PorterDuff.Mode.MULTIPLY
        )
        flightWeightTxt.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
        flightWeightIm.setColorFilter(ContextCompat.getColor(requireContext(), color))
        flightWeightImCon.setColorFilter(ContextCompat.getColor(requireContext(), colorCon))
    }
}
