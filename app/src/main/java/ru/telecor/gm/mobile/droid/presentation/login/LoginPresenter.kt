package ru.telecor.gm.mobile.droid.presentation.login

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.FIREBASE_DRIVER_NUMBER_KEY
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.LoginRequest
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.interactors.AuthInteractor
import ru.telecor.gm.mobile.droid.model.interactors.ReportLoginInterator
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import java.lang.Exception
import javax.inject.Inject

@InjectViewState
class LoginPresenter @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val commonDataRepository: CommonDataRepository,
    private val reportLoginInterator: ReportLoginInterator,
    private val rm: IResourceManager,
    val settingsPrefs: SettingsPrefs,
    private val gmServerPrefs: GmServerPrefs,
) : BasePresenter<LoginView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
        viewState.setCheckingVersion(
            BuildConfig.VERSION_NAME,
            settingsPrefs.isLatestVersion,
            settingsPrefs.isInstallationRole,
            settingsPrefs.visibilityNext,
            settingsPrefs.isInstallationComplete
        )
    }

    fun loginButtonPressed(
        personnelNumber: String,
        loginData: LoginRequest,
        phoneInformation: String,
        codename: String,
        device: String
    ) {
        FirebaseCrashlytics.getInstance().setCustomKey(FIREBASE_DRIVER_NUMBER_KEY, personnelNumber)
        if (personnelNumber == "") {
            viewState.setPersonnelNumberFieldEmptyError()
        } else {
            viewState.setLoadingState(true)
            try {
                launch {
                    reportLoginInterator.sendReport(personnelNumber)
                }
            } catch (e: Exception) {
                LogUtils.error(javaClass.simpleName, e.message)
            }
            commonDataRepository.checkForUpdates(
                {
                    launch {
                        viewState.showUpdateDialog(BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion))
                        viewState.setLoadingState(false)
                    }
                },
                {
                    launch {
                        login(personnelNumber, loginData, phoneInformation, codename, device)
                        viewState.setLoadingState(false)
                    }
                },
                ConnectivityUtils.syncAvailability(
                    reportLoginInterator.getContext(),
                    ConnectivityUtils.DataType.SECONDARY
                )
            )

//            launch {
//                val res = commonDataRepository.getLatestVersionInfo()
//                handleResult(res, {
//                    if (it.data.toString() != BuildConfig.VERSION_NAME
//                    ) {
//                        viewState.showUpdateDialog()
//                        viewState.setLoadingState(false)
//                    } else {
//                        launch {
//                            login(personnelNumber, loginData)
//                            viewState.setLoadingState(false)
//                        }
//                    }
//                }, { handleError(it, rm) })
//            }
        }
    }

    fun isVisibilityNext(boolean: Int) {
        settingsPrefs.visibilityNext = boolean
    }

    private suspend fun login(
        personnelNumber: String,
        loginData: LoginRequest,
        androidVirsion: String,
        codename: String,
        device: String
    ) {
        val result =
            authInteractor.login(personnelNumber, loginData, androidVirsion, codename, device)
        handleResult(result, {
            viewState.setLoadingState(false)
            viewState.openMainScreen()

            // say hello to driver
            val driverName: String? = it.data.driver.let { driver ->
                driver.firstName ?: driver.lastName ?: driver.middleName
            }

            settingsPrefs.staffId = it.data.driver.id

            if (driverName != null) {
//                viewState.showMessage(
//                    rm.getString(R.string.login_hello)
//                        .replace("%s", driverName)
//                )
            } else {
                viewState.showMessage(rm.getString(R.string.login_hello_simply))
            }
        }, {
            viewState.setLoadingState(false)
            handleError2(it, rm) { msg ->
                viewState.showErrorMessage(msg)
            }
        })
    }

//    fun bottomFragment(fragment: LowerFragment, activity: AppCompatActivity) {
//        return when(fragment){
//            NEW -> {
//                val bottomSheetDialogFragment = NewBottomSheetFragment()
//                bottomSheetDialogFragment.isCancelable = false
//                bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
//            }
//            FINISH -> {
//                val bottomSheetDialogFragment = FinishedBottomSheetFragment()
//                bottomSheetDialogFragment.isCancelable = false
//                bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
//            }
//            POST -> {
//                val bottomSheetDialogFragment = LastBottomSheetFragment()
//                bottomSheetDialogFragment.isCancelable = false
//                bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
//            }
//        }
//    }
//
//    enum class LowerFragment{
//        NEW,
//        FINISH,
//        POST
//    }

    fun onPermissionsDenied() {
        viewState.showMessage(rm.getString(R.string.error_permission_denied))
    }

    fun onAboutButtonClicked() {
        viewState.openAboutScreen()
    }

    fun onSettingButtonClicked() {
        viewState.showConfirmPasswordDialog()
    }

    fun onErrorDetailedInfoClicked(ex: Throwable) {
        viewState.showErrorDialog(ex)
    }

    fun onSendReportDialogButtonClicked(ex: Throwable) {}

    fun onConfirmPasswordClicked(password: String? = null) {
        viewState.openSettingsScreen()
//        when {
//            password.isNullOrEmpty() || password != BuildConfig.PASSWORD -> {
//                viewState.showConfirmPasswordDialog()
//            }
//            password == BuildConfig.PASSWORD -> {
//                viewState.openSettingsScreen()
//            }
//        }
    }

    fun onUpdateDialogConfirmed() {
        viewState.openAboutScreen()
    }
}
