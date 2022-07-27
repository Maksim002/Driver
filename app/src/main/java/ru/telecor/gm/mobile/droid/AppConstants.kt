package ru.telecor.gm.mobile.droid

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.05.2021
 * Copyright © 2020 TKOInform. All rights reserved.
 */
object AppConstants {

    const val YANDEX_CLIENT = "179"
    const val YANDEX_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAxUDOfNablcj2SBI9\n" +
            "KgEQ8TqPh6zw9NthcMlx6Dd6z7fDDyq0c/EJfPuDFe0jL3CvXMcFouY2wszNzmKp\n" +
            "jqanTQIDAQABAkA+TYXq8G4cFWmUwg4HomaTiweitwE0lcZlNXuA+WUVDktU/dAg\n" +
            "4kmAlQNHx7dh1J5edwl9P+garmegoPHr1BSBAiEA9Q0aBWvlbC51F460OVGV3TUq\n" +
            "m1ToSTuxEgEc8H39PxkCIQDOEP2TMlxRVkKzACLPumLCFrmNhdMe6jZeXhv9RdDU\n" +
            "VQIgPrCxXX2y3bAs6G/rj1Zd3o+BbOeV3VZWAGMkginZJdECIG0jRontREO34f+X\n" +
            "9Nf8KU4OZxvzYsue1EjKoxxTG7U5AiEA1FHzZPJ40GHXWwKt95cRluiGXnS1XOeb\n" +
            "C2O2akQy3Uw=\n" +
            "-----END PRIVATE KEY-----"
    const val YANDEX_NAVI_PACKAGE = "ru.yandex.yandexnavi"

    internal val APP_DB_NAME = "gm_app_database.db"
    internal val EMPTY_EMAIL_ERROR = 1001
    internal val INVALID_EMAIL_ERROR = 1002
    internal val EMPTY_PASSWORD_ERROR = 1003
    internal val LOGIN_FAILURE = 1004
    internal val NULL_INDEX = -1L

    enum class LoggedInMode constructor(val type: Int) {
        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3)
    }
}