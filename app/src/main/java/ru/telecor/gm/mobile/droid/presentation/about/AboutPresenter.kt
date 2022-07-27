package ru.telecor.gm.mobile.droid.presentation.about

import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class AboutPresenter @Inject constructor(
    private val commonDataRepository: CommonDataRepository,
    private val rm: IResourceManager,
    private val gmServerPrefs: GmServerPrefs
) : BasePresenter<AboutView>() {

    var cVersion = BuildVersion.ALPHA

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        cVersion = BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
        checkForUpdates(BuildVersion.ALPHA)
        checkForUpdates(BuildVersion.BETA)
    }

    private fun checkForUpdates(version: BuildVersion = cVersion, autoCheck: Boolean = true) {
        viewState.setNewestVersion("Ожидайте...", version)
        viewState.setLoadingState(true, version)
        launch {
            val res = commonDataRepository.getLatestVersionInfo(version)
            handleResult(res, {
                viewState.setNewestVersion(it.data.toString(), version)
                if (!autoCheck) {
                    if (version.localName != gmServerPrefs.getGmBuildCon().buildVersion){
                        viewState.showUpdateDialog(version)
                    }else if (it.data.toString() != BuildConfig.VERSION_NAME && version == BuildVersion.BETA){
                        viewState.showUpdateDialog(version)
                    }
                } else {
                    if (it.data.biggerThan(BuildConfig.VERSION_NAME) && version == BuildVersion.ALPHA) {
                        viewState.showUpdateDialog(version)
                    }
                }
            }, { handleError(it, rm) })
            viewState.setLoadingState(false, version)
        }
    }

    fun onBackButtonClicked() {
        viewState.finishScreen()
    }

    fun onUpdateButtonClicked(version: BuildVersion, autoCheck: Boolean) {
        checkForUpdates(version, autoCheck)
    }

    fun onUpdateAccepted() {
        launch {
            val path =
                commonDataRepository.downloadLatestVersion(BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion).serverName)
            handleResult(path, { viewState.installAPK(it.data) }, { handleError(it, rm) })
        }
    }

}
