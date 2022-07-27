package ru.telecor.gm.mobile.droid.di.modules

import com.google.gson.Gson
import okhttp3.OkHttpClient
import ru.telecor.gm.mobile.droid.di.ServerPath
import ru.telecor.gm.mobile.droid.di.providers.*
import ru.telecor.gm.mobile.droid.model.data.db.dao.CommonDataDao
import ru.telecor.gm.mobile.droid.model.data.db.dao.TourDao
import ru.telecor.gm.mobile.droid.model.data.server.GmWebSocket
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApi
import ru.telecor.gm.mobile.droid.model.data.server.TruckCrewApiLongRequest
import ru.telecor.gm.mobile.droid.model.data.storage.LocalDataPrefs
import ru.telecor.gm.mobile.droid.model.interactors.*
import ru.telecor.gm.mobile.droid.model.mappers.ConverterMappers
import ru.telecor.gm.mobile.droid.model.repository.AuthRepository
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.repository.PhotoRepository
import ru.telecor.gm.mobile.droid.model.repository.RouteRepository
import toothpick.config.Module

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.di.modules
 *
 * Server scope dependencies.
 *
 * Created by Artem Skopincev (aka sharpyx) 27.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class ServerModule : Module() {

    init {
        bind(String::class.java).withName(ServerPath::class.java)
            .toProvider(ServerUrlProvider::class.java).providesSingleton()
        bind(Gson::class.java).toProvider(GsonProvider::class.java).providesSingleton()
        bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java)
            .providesSingleton()
        bind(TruckCrewApi::class.java).toProvider(ApiProvider::class.java).providesSingleton()
        bind(TruckCrewApiLongRequest::class.java).toProvider(ApiProviderLongRequest::class.java).providesSingleton()

        bind(LocalDataPrefs::class.java).singleton()
        bind(TourDao::class.java).toProvider(TourDaoProvider::class.java)
        bind(CommonDataDao::class.java).toProvider(CommonDaoProvider::class.java)

        bind(GmWebSocket::class.java).toProvider(GmWebSocketProvider::class.java)

        bind(AuthRepository::class.java).singleton()
        bind(AuthInteractor::class.java).singleton()

        bind(RouteRepository::class.java).singleton()
        bind(RouteInteractor::class.java).singleton()

        bind(PhotoRepository::class.java).singleton()
        bind(PhotoInteractor::class.java).singleton()

        bind(ConverterMappers::class.java).singleton()

        bind(CommonDataRepository::class.java).singleton()

        bind(AppInteractor::class.java).singleton()

        // Report Photos Interactors
        bind(ReportRoutePhotosInteractor::class.java).singleton()
        bind(ReportOnPhotosFromServer::class.java).singleton()
        bind(ReportRouteAllPhotosInteractor::class.java).singleton()
        bind(ReportRoutesPhotosWithTimeInteractor::class.java).singleton()

        // Report Auth Interactors
        bind(ReportLoginInterator::class.java).singleton()
        bind(ReportLogoutInterator::class.java).singleton()
    }
}