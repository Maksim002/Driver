package ru.telecor.gm.mobile.droid.model.data.server

import retrofit2.http.GET
import retrofit2.http.Path
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.entities.task.Task

interface TruckCrewApiLongRequest {

    @GET("route/{routeId}/stands")
    suspend fun getRouteStands(@Path("routeId") routeId: Long): List<Stand>

    @GET("route/{routeId}/tasks")
    suspend fun getRouteTasks(@Path("routeId") routeId: Long): List<Task>
}