package ru.telecor.gm.mobile.droid.presentation.updateDialog

import kotlinx.coroutines.launch
import ru.telecor.gm.mobile.droid.model.BuildCon
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

class UpdateDialogPresenter @Inject constructor(
    private var commonDataRepository: CommonDataRepository,
    private var rm: IResourceManager,
    private var serverPrefs: GmServerPrefs,
    var prefs: SettingsPrefs,
    val gmServerPrefs: GmServerPrefs,
) : BasePresenter<UpdateDialogView>() {

    var version = "droid-team-v4"
    var install = true

    var cVersion = BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (version != BuildVersion.fromName(serverPrefs.getGmBuildCon().buildVersion).serverName) {
            install = false
            val checkedVersion = BuildVersion.fromServerName(version)
            viewState.switchingVersions(checkedVersion)
        }
    }

    fun onUpdateAccepted() {
        launch {
            viewState.setLoadingState(true)
            val path = commonDataRepository.downloadLatestVersion(version)
            handleResult(path, {
                serverPrefs.setGmBuildCon(BuildCon(buildVersion = BuildVersion.fromServerName(version).localName))
                viewState.installAPK(it.data)
                viewState.setLoadingState(false)
                viewState.installationAPK()
                if (install){
                    checkForUpdates()
                }
            }, {
                handleError(it, rm)
                viewState.setLoadingState(false)
            })
        }
    }

    private fun checkForUpdates() {
        launch {
            val res = commonDataRepository.getLatestVersionInfo(cVersion)
            handleResult(res, {
                prefs.isLatestVersion = it.data.toString()
            }, { handleError(it, rm) })
        }
    }

    fun deleteVersion(){
        prefs.isLatestVersion = ""
    }
}