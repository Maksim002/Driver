package ru.telecor.gm.mobile.droid.presentation.serverSettingsN

import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.entities.GmServerInfo
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import java.net.URL
import javax.inject.Inject

@InjectViewState
class ServerSettingsBottomPresenter @Inject constructor(
    private val gmServerPrefs: GmServerPrefs,
    private val settingsPrefs: SettingsPrefs,
) : BasePresenter<ServerSettingsBottomView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)

        val info = GmServerInfo(
            BuildConfig.SERVER_PROTOCOL,
            BuildConfig.SERVER_HOST,
            BuildConfig.SERVER_PORT.toInt(),
            BuildConfig.SERVER_APPLICATION
        )

        val newInfo = gmServerPrefs.getGmServerInfo()

        viewState.showCurrentServerInfo(newInfo ?: info)
    }

    fun saveButtonPressed(protocol: String, server: String, port: String, application: String) {
        try {
            val portInt = port.toInt()
            if (portInt != 0) {
                URL(protocol, server, portInt, application)
            } else {
                URL(protocol, server, application)
            }
        } catch (e: Exception) {
            viewState.setTextError(true)
            viewState.showMessage("Не удается сформировать URL. Проверьте данные")
            return
        }
        when {
            protocol == "" -> {
                viewState.setEmptyProtocolFieldError()
                return
            }
            server == "" -> {
                viewState.setEmptyServerFieldError()
                return
            }
            port == "" -> {
                viewState.setEmptyPortFieldError()
                return
            }
            application == "" -> {
                viewState.setEmptyApplicationFieldError()
                return
            }
        }

        gmServerPrefs.setGmServerInfo(GmServerInfo(protocol, server, port.toInt(), application))
        viewState.isDismiss()
    }
}
