package ru.telecor.gm.mobile.droid.entities

import ru.telecor.gm.mobile.droid.entities.db.TaskDraftProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.task.ContainerAction
import ru.telecor.gm.mobile.droid.entities.task.TaskItem
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations

data class TaskItemPreviewData(
    val containerType: ContainerType,
    val garbageType: GarbageType,
    val containerAction: ContainerAction?,
    var count: Int,
    var supportedGarbageType: Boolean = true,
    var taskRelations: TaskRelations? = null,
    var task: List<TaskItem>? = null,
    var statusType: String? = null,
    var failureReason: String? = null,
    var taskDraftData: TaskDraftProcessingResult? = null,
    var taskResultData: TaskProcessingResult? = null
)