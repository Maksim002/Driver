package ru.telecor.gm.mobile.droid.di.providers

import ru.telecor.gm.mobile.droid.di.ServerPath
import ru.telecor.gm.mobile.droid.model.data.server.GmWebSocket
import ru.telecor.gm.mobile.droid.model.data.storage.AuthHolder
import ru.telecor.gm.mobile.droid.model.system.SecurityUtils
import javax.inject.Inject
import javax.inject.Provider

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.providers
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 29.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class GmWebSocketProvider @Inject constructor(
    @ServerPath private val serverUri: String,
    private val securityUtils: SecurityUtils,
    private val authHolder: AuthHolder
) : Provider<GmWebSocket> {

    override fun get(): GmWebSocket {
        return GmWebSocket(serverUri, securityUtils, authHolder)
    }
}