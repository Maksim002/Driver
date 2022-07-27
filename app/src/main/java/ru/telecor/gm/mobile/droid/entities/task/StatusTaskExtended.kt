package ru.telecor.gm.mobile.droid.entities.task

import ru.telecor.gm.mobile.droid.entities.ContainerType
import ru.telecor.gm.mobile.droid.entities.GarbageType
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.processing.ContainerStatus

data class StatusTaskExtended(
    val id: Int,

    val containerType: ContainerType,

    val taskItemId: Int,

    val containerStatus: ContainerStatus? = null,

    val draftData: ContainerStatus? = null,

    val rule: String? = null,

    val containerAction: String? = null,

    val containerGroups: GarbageType? = null,

    val taskStatus: TaskFailureReason? = null
)