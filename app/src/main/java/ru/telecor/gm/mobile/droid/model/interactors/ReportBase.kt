package ru.telecor.gm.mobile.droid.model.interactors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportBase @Inject constructor(
    private var reportRoutePhotosInteractor: ReportRoutePhotosInteractor,
    //private var reportRouteAllPhotosInteractor: ReportRouteAllPhotosInteractor,
    private var reportRoutesPhotosWithTimeInteractor: ReportRoutesPhotosWithTimeInteractor
) {
    fun sendReport(routeId: Long? = null, routeInteractor: RouteInteractor, time: Boolean = false) {
        GlobalScope.launch {
            if (routeId != null) {
                if (time) reportRoutesPhotosWithTimeInteractor.senReport(
                    routeId,
                    routeInteractor
                ) else reportRoutePhotosInteractor.sendApplicationReasonUndeliveredPhotos(
                    routeId,
                    routeInteractor
                )
            }
            // reportRouteAllPhotosInteractor.sendApplicationReasonUndeliveredPhotos(routeInteractor)
        }
    }
}