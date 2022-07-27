package ru.telecor.gm.mobile.droid.model.data.server

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import ru.telecor.gm.mobile.droid.entities.*
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.request.*
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.entities.task.Task
import ru.telecor.gm.mobile.droid.model.repository.mod.OptionsModel
import java.io.File

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.server
 *
 * API interface (Retrofit).
 *
 * Created by Artem Skopincev (aka sharpyx) 16.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface TruckCrewApi {

    // region Auth
    @POST("login3")
    suspend fun login(@Body loginData: LoginRequest): TourInfo
    // endregion

    // region Route
    @POST("route/{routeId}/start")
    suspend fun startRoute(@Path("routeId") routeId: Long): RouteInfo

    @POST("route/{routeId}/continue")
    suspend fun continueRoute(@Path("routeId") routeId: Long): RouteInfo

    @POST("route/{routeId}/reorder")
    suspend fun reorderRoute(
        @Path("routeId") routeId: Long,
        @Body standReorderData: StandReorderData
    ): ResponseBody

    @POST("route/{routeId}/finish")
    suspend fun finishRoute(
        @Path("routeId") routeId: Long,
        @Body finishRouteData: FinishRouteData
    ): ResponseBody


    //Cancel the next unloading location
    @POST("route/{routeId}/polygon-cancel")
    suspend fun polygonCancel(
        @Path("routeId") routeId: Int
    ): ResponseBody



    @POST("route/{routeId}/prepareFinish")
    suspend fun prepareFinishRoute(
        @Path("routeId") routeId: Long
    ): ResponseBody

    @POST("route/{routeId}/pushOfflineResults")
    suspend fun pushOfflineResult(
        @Path("routeId") routeId: Long,
        @Body results: List<TaskProcessingResult>
    ): ResponseBody

    @POST("route/{routeId}/polygon")
    suspend fun polygon(@Path("routeId") routeId: Long, @Body polygonRequest: PolygonRequest): Long
    // endregion

    // region Stand and Tasks
    @GET("route/{routeId}/stands")
    suspend fun getRouteStands(@Path("routeId") routeId: Long): List<Stand>

    @GET("route/{routeId}/tasks")
    suspend fun getRouteTasks(@Path("routeId") routeId: Long): List<Task>
    // endregion

    // region Porters
    @GET("porters")
    suspend fun getPorters(
        @Query("includePhoto") includePhoto: Boolean,
        @Query("synchronizationTime") synchronizationTime: String): List<LoaderInfo>

    @POST("route/{routeId}/setPorter")
    suspend fun setPorter(
        @Path("routeId") routeId: Long,
        @Body porterData: PorterData
    ): ResponseBody
    // endregion

    // region Synchronization
    @GET("processing/routeTaskFailureReasons")
    suspend fun getTaskFailureReasons(): List<TaskFailureReason>

    @GET("processing/containerFailureReasons")
    suspend fun getContainerTroubleReasons(): List<ContainerFailureReason>

    @GET("processing/visitPoints")
    suspend fun getVisitPoints(): List<VisitPoint>
    // endregion

    // region containers history

    @POST("route/{routeId}/containers/management/action")
    suspend fun pushHistoryItem(
        @Path("routeId") routeId: Long,
        @Body carryContainerHistoryItemSendable: CarryContainerHistoryItemSendable
    ): ResponseBody

    @GET("route/{routeId}/containers/management/history")
    suspend fun getContainersHistory(@Path("routeId") routeId: Long): List<CarryContainerHistoryItemSendable>

    @GET("route/{routeId}/containers/management/currentContainers")
    suspend fun getCurrentContainers(@Path("routeId") routeId: Long): List<CarryContainer>

    //Employee photo Request
    @GET("staff/{staffId}/photo")
    suspend fun getPhotoRequest(@Path("staffId") staffId: String): ResponseBody

    // endregion

    @POST("routeTask/uploadPhoto")
    suspend fun uploadTaskPhoto(@Body requestBody: RequestBody): ResponseBody

    @POST("upload/db")
    suspend fun uploadDatabase(@Body file: File): ResponseBody

    @POST("route/{routeId}/polygon")
    suspend fun polygon(@Path("routeId") routeId: Int, @Body polygonData: PolygonRequest): Int

    @POST("processing/arrival")
    suspend fun arrival(@Body arrivalRequest: ArrivalRequest): ResponseBody

    @POST("route/{routeId}/heartbeat")
    suspend fun heartbeat(@Path("routeId") routeId: Long): ResponseBody

    @GET("droid/version")
    suspend fun getLatestVersionInfo(@Query("code") version: String): VersionData

    @Streaming
    @GET("droid/next")
    suspend fun latestVersion(@Query("code") version: String): ResponseBody

    @GET("nearestStands")
    suspend fun getNearestStands(@Query("position") position: PositionQuery): List<Stand>

    @GET("options")
    suspend fun getMobileSystemOptions(): MobileSystemOptions

    @GET("route/needReloadPhotos")
    suspend fun getNeededPhotosToReload(@Query("routeIds") routeIds: String): List<String>

    @POST("undeliveredPhotos")
    suspend fun getUndeliveredPhotosFromList(@Body array: List<String>): List<String>

    @GET("options")
    suspend fun options(): OptionsModel

    @GET("route/{id}/coupons")
    suspend fun flightTicket(@Path("id") id: Long): List<GetListCouponsModel>

}