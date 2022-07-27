package ru.telecor.gm.mobile.droid.presentation.updateActivity

import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.model.BuildCon
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class UpdatingPresenter  @Inject constructor(
    private var commonDataRepository: CommonDataRepository,
    private var rm: IResourceManager,
    private var serverPrefs: GmServerPrefs,
    private var prefs: SettingsPrefs,
    val gmServerPrefs: GmServerPrefs,
) : BasePresenter<UpdatingView>() {
    var cVersion = BuildVersion.ALPHA

    var versions = "alpha"
    var install = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        cVersion = BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)
        checkForUpdates()
    }

    private fun checkForUpdates(version: BuildVersion = cVersion, autoCheck: Boolean = true) {
        viewState.setNewestVersion("Ожидайте...", version)
        viewState.setLoadingState(true, version)
        launch {
            val res = commonDataRepository.getLatestVersionInfo(version)
            handleResult(res, {
                viewState.setNewestVersion(it.data.toString(), version)
                setLatestVersion(it.data.toString())
                viewState.showUpdateDialog(version)
            }, { handleError(it, rm) })
            viewState.setLoadingState(false, version)
        }
    }

    private fun setLatestVersion(string: String){
        prefs.isLatestVersion = string
    }

    fun onUpdateAccepted() {
        launch {
            viewState.setLoadingState(true)
            val path = commonDataRepository.downloadLatestVersion(versions)
            handleResult(path, {
                serverPrefs.setGmBuildCon(BuildCon(buildVersion = BuildVersion.fromServerName(versions).localName))
                viewState.installAPK(it.data)
                viewState.setLoadingState(false)
                viewState.installationAPK()
            }, {
                handleError(it, rm)
                viewState.setLoadingState(false)
            })
        }
    }

    fun deleteVersion(){
        prefs.isLatestVersion = ""
    }
}
