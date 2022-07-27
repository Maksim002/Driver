package ru.telecor.gm.mobile.droid.utils

import com.google.gson.GsonBuilder

class GsonUtils {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()


    fun isJSONValid(json: String?): Boolean {
        try {
            if (json == null || json.isEmpty()) {
                return false
            } else if (!json.trim { it <= ' ' }.startsWith("{")) {
                return false
            }

            gson.fromJson(json, Any::class.java)

            return true
        } catch (ex: com.google.gson.JsonSyntaxException) {
            return false
        }

    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }
}