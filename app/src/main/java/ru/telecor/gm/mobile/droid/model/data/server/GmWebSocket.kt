package ru.telecor.gm.mobile.droid.model.data.server

import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import ru.telecor.gm.mobile.droid.di.ServerPath
import ru.telecor.gm.mobile.droid.entities.GmSocketResponse
import ru.telecor.gm.mobile.droid.entities.RouteDataMsg
import ru.telecor.gm.mobile.droid.entities.WebSocketMsg
import ru.telecor.gm.mobile.droid.entities.WebSocketTalon
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.repository.RouteRepository
import ru.telecor.gm.mobile.droid.model.system.SecurityUtils
import ru.telecor.gm.mobile.droid.utils.GsonUtils
import ru.telecor.gm.mobile.droid.utils.LogUtils
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 25.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class GmWebSocket @Inject constructor(
    @ServerPath private val serverPath: String,
    private val securityUtils: SecurityUtils,
    private val authHolder: AuthHolder
) : WebSocketListener() {

    private var client: OkHttpClient?= null
    private var request: Request
    private var currentWebSocket: WebSocket? = null
    private var onMessageReceived: ((webSocketMessage: WebSocketMsg) -> Unit)? = null
    private var onOpenListener: (() -> Unit)? = null
    private var onCloseListener: (() -> Unit)? = null
    private var onTalonListener: ((webSocketTalon: WebSocketTalon) -> Unit)? = null
    var gsonUtils = GsonUtils()

    init {
        val path = serverPath + "notification/mobile"

        client = with(OkHttpClient.Builder()) {
            readTimeout(30000, TimeUnit.MILLISECONDS)
            connectTimeout(30000, TimeUnit.MILLISECONDS)
            retryOnConnectionFailure(true)
            connectionPool(ConnectionPool(1,300L,TimeUnit.MINUTES))
            build()
        }

        val hash = securityUtils.getBasicAuthHash(
            authHolder.personnelNumber ?: "",
            SecurityUtils.StandardPassword
        ).replace("\n", "")

        request = Request.Builder()
            .url(path)
            .addHeader("Authorization", "Basic $hash")
            .build()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        onOpenListener?.invoke()
        Timber.d("WebSocket: open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)

        if (text == "PONG") {
            webSocket.send("PING")
//            Timber.d("WebSocket: sending = PING")
            return
        }
        try {
            if (gsonUtils.isJSONValid(text)) {
                val msg = Gson().fromJson(text, WebSocketMsg::class.java)
                val msgTalon = Gson().fromJson(text, WebSocketTalon::class.java)

                if (msgTalon.command?.action != CommandActionEnum.attachWeighing.toString()){
                    if (msg.command.action == CommandActionEnum.update) {
                        onMessageReceived?.invoke(msg)
                    }

                    val gmResponse = Gson().toJson(GmSocketResponse(msg.id, msg.unitId))
                    webSocket.send(gmResponse)
                }else if (msgTalon.command?.action == CommandActionEnum.attachWeighing.toString()){
                    onTalonListener?.invoke(msgTalon)

                    val gmResponse = Gson().toJson(GmSocketResponse(msgTalon.id ?: "", msgTalon.unitId ?: ""))
                    webSocket.send(gmResponse)
                }
            } else {
                webSocket.send("PING")
            }
        } catch (e: Exception) {
            webSocket.send("PING")
        }

        Timber.d("WebSocket: message ${text.length}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)

        Timber.d("WebSocket: closing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)

        onCloseListener?.invoke()
        Timber.d("WebSocket: closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)

        Timber.d("WebSocket: failure ${t.message}")

        onCloseListener?.invoke()
        webSocket.close(1000, null)
        currentWebSocket = null
    }

    fun setOnOpenListener(action: () -> Unit) {
        onOpenListener = action
    }

    fun setOnCloseListener(action: () -> Unit) {
        onCloseListener = action
    }

    fun addOnMessageReceivedListener(listener: (msg: WebSocketMsg) -> Unit) {
        onMessageReceived = listener
    }

    fun addOnMessageTalonListener(listener: (msg: WebSocketTalon) -> Unit) {
        onTalonListener = listener
    }

    @DelicateCoroutinesApi
    fun startListen() {
        for (i in 0..4) {
            try {
                GlobalScope.launch {
                    currentWebSocket = client?.newWebSocket(request, this@GmWebSocket)
                    currentWebSocket?.request()
                    onOpenListener?.invoke()
                    currentWebSocket?.send("PING")
                }
                break
                // client.dispatcher().executorService().shutdown()
            } catch (e: Exception) {
                currentWebSocket
            }
        }
    }

    @DelicateCoroutinesApi
    fun checkListen() = if (currentWebSocket != null) {
        currentWebSocket
    } else{
        startListen()
    }

    fun close() {
//        currentWebSocket?.let {
//            super.onClosing(currentWebSocket!!, 1000, "logout")
//        }
        //client?.dispatcher?.executorService?.shutdown()
        client?.dispatcher?.cancelAll()
    }
}