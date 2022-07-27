package ru.telecor.gm.mobile.droid.entities

data class ContainerLoadLevel(val title: String, val value: Double, val type: Type) {
    enum class Type {
        FULL,
        OVERFLOWING,
        PARTIAL,
        HALF,
        FOURTH,
        EMPTY,
        FAILURE
    }
}
