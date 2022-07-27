package ru.telecor.gm.mobile.droid.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.main.MainPresenter
import ru.telecor.gm.mobile.droid.presentation.main.MainView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.utils.LogUtils
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import toothpick.Toothpick
import java.lang.Exception
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView {

    override val layoutResId = R.layout.activity_main

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(MainPresenter::class.java)
    }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

//        this.recreate()
    }

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.flFragmentHolder) {
            override fun setupFragmentTransaction(
                command: Command,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction,
            ) {
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    private var networkInfo: NetworkInfo? = null

    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            presenter.updateBatteryLevel(level.toString() + "%")
        }
    }

    private val timeBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            presenter.updateTime()
        }
    }

    private val internetBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val cm: ConnectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkInfo = cm.activeNetworkInfo
            presenter.updateInternetStatus(networkInfo?.isConnected ?: false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        this.registerReceiver(timeBroadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        this.registerReceiver(
            internetBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

//        btnHome.setOnClickListener {
//            presenter.onHomeButtonClicked()
//        }
//
//        btnSettings.setOnClickListener { presenter.onSettingsClicked() }

        Toothpick.inject(this, Toothpick.openScopes(Scopes.APP_SCOPE, Scopes.SERVER_SCOPE))

//        if (presenter.flightTicketModel.value != null) {
//            initBottomSheet(presenter.flightTicketModel)
//        }
    }

    override fun updateLocationProvidersBooleans() {
        super.updateLocationProvidersBooleans()
        presenter.updateLocationState(isNetworkProviderOn || isGPSProviderOn)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()

        super.onPause()
    }
    // endregion

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryBroadcastReceiver)
        unregisterReceiver(timeBroadcastReceiver)
        unregisterReceiver(internetBroadcastReceiver)
    }

//    private fun initBottomSheet(res: MutableLiveData<GetListCouponsModel>) {
//        val stepBottomFragment = MainBottomFragment(res)
//        stepBottomFragment.isCancelable = false
//        stepBottomFragment.show(this.supportFragmentManager, stepBottomFragment.tag)
//    }

    override fun showLocationState(value: Boolean) {
//        if (value) {
//            imgGpsIndicator.setImageResource(R.drawable.ic_baseline_gps_fixed_24)
//        } else {
//            imgGpsIndicator.setImageResource(R.drawable.ic_baseline_gps_not_fixed_24)
//        }
    }

    override fun showBatteryLevel(string: String) {
//        tvBattery.text = string
    }

    override fun showCurrentTime(string: String) {
//        tvClock.text = string
    }

    override fun showInternetConnection(connectionAvailable: Boolean) {
//        if (connectionAvailable) {
//            imgInternetIndicator.setImageResource(R.drawable.ic_baseline_wifi_24)
//        } else {
//            imgInternetIndicator.setImageResource(R.drawable.ic_baseline_wifi_off_24)
//        }
    }

    override fun setConnectionEstablishedState(isConnected: Boolean) {
//        try {
//            GlobalScope.launch(Dispatchers.Main) {
//                if (isConnected) {
//                    imgHeartbeatIndicator.setColorFilter(this@MainActivity.color(R.color.grey_color))
//                } else {
//                    imgHeartbeatIndicator.setColorFilter(this@MainActivity.color(R.color.colorHeartbeatTintRed))
//                }
//            }
//        } catch (e: Exception) {
//            LogUtils.error(javaClass.simpleName, e.message)
//        }
    }

    override fun setDataExportingState(isExporting: Boolean) {
        try {
            MainScope().launch {
//                imgHeartbeatIndicator.visible(!isExporting, View.INVISIBLE)
//                pbExporting.visible(isExporting)

            }
        } catch (e: Exception) {
            LogUtils.error(javaClass.simpleName, e.message)
        }
    }

    override fun showSettingsMenu() {
        presenter.isVisibilityNext(View.VISIBLE)
        val bottomSheetDialogFragment = SettingBottomSheetFragment()
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)

//        val sMenu = PopupMenu(this, btnSettings).apply {
//            val inflater = menuInflater
//            inflater.inflate(R.menu.main_settings_menu, menu)
//            setOnMenuItemClickListener {
//                when (it.itemId) {
//                    R.id.menuSettingsLogOut -> presenter.onLogOutClicked()
//                    R.id.menuSettingsAbout -> presenter.onAboutClicked()
//                    R.id.menuSettingsReportBug -> presenter.onReportBugClicked()
//                }
//                return@setOnMenuItemClickListener true
//            }
//        }
//        sMenu.show()
    }

    override fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.logOut_dialog_title)
            setMessage(R.string.logOut_dialog_message)
            setPositiveButton(R.string.logOut_dialog_yes) { _, _ -> presenter.onLogOutConfirmed() }
            setNegativeButton(R.string.logOut_dialog_cancel) { _, _ -> }
        }.show()
    }

    override fun setContainersCount(count: Int) {
//        tvContainersCount.text = count.toString()
    }
}
