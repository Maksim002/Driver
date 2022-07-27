package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo

data class TourInfo(
    @SerializedName("driver")
    val driver: DriverInfo,
    @SerializedName("delayedRoutes")
    val delayedRoutes: List<RouteInfo>,
    @SerializedName("showEmptyContainer")
    val showEmptyContainer: Boolean,
    @SerializedName("nearestStandSearchDistance")
    val nearestStandSearchDistance: Int,
    @SerializedName("showNearestTaskStandSearch")
    val showNearestTaskStandSearch: Boolean,
    @SerializedName("nearestTaskStandSearchRadius")
    val nearestTaskStandSearchRadius: Int,
    @SerializedName("permissions")
    val permissions: List<String>
)