package ru.telecor.gm.mobile.droid.model.data.server

import java.lang.RuntimeException

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server
 *
 * Class displays ServerError.
 * @param errorCode Server response code
 * @param errorResponse API error response
 *
 * Created by Artem Skopincev (aka sharpyx) 06.08.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ServerError(
    val errorCode: Int,
    val errorResponse: ApiErrorResponse?
) : RuntimeException()