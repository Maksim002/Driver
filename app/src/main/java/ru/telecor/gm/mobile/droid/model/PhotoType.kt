package ru.telecor.gm.mobile.droid.model

enum class PhotoType(val photoTag: String, val serverName: String, val viewName: String) {
    TASK_TROUBLE("TTR", "ROUTE_TASK_FAILURE_REASON_PHOTO","Невывоз"),
    LOAD_BEFORE("LB", "PHOTO_BEFORE_SHIPPING","До погрузки"),
    LOAD_AFTER("LA", "PHOTO_AFTER_SHIPPING","После погрузки"),
    LOAD_TROUBLE("LT", "ROUTE_TASK_ACTUAL_REASON_PHOTO","Проблемы с погрузкой"),
    LOAD_TROUBLE_BLOCKAGE("LTB", "ROUTE_TASK_ACTUAL_REASON_PHOTO","Проблемы с погрузкой");

    companion object {
        fun fromPhotoTag(tag: String): PhotoType {
            when (tag) {
                "TTR" -> return TASK_TROUBLE
                "LB" -> return LOAD_BEFORE
                "LA" -> return LOAD_AFTER
                "LT" -> return LOAD_TROUBLE
                "LTB" -> return LOAD_TROUBLE_BLOCKAGE
            }

            throw Exception("not found")
        }
    }
}