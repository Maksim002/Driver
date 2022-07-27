package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

enum class ActualReason(text: String) {
    @SerializedName("BULK")
    BULK("Навал мусора на площадке"),

    @SerializedName("CONTAINER_FAILURE")
    CONTAINER_FAILURE("Невозможно вывезти контейнер(ы)")
}