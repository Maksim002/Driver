package ru.telecor.gm.mobile.droid.model.data.db.dao

import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.VisitPoint

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.data.db.dao
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 14.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface CommonDataDao {

    fun getLoaders(): List<LoaderInfo>?
    fun setLoadersList(list: List<LoaderInfo>)

    fun getVisitPoints(): List<VisitPoint>?
    fun setVisitPointsList(list: List<VisitPoint>)

    fun setContainerFailureReasons(list: List<ContainerFailureReason>)
    fun getContainerFailureReasons(): List<ContainerFailureReason>?

    fun setTaskFailureReasons(list: List<TaskFailureReason>)
    fun getTaskFailureReasons(): List<TaskFailureReason>?
}