package ru.telecor.gm.mobile.droid.entities.task

import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.task.TaskItem

class GeneralTaskModel(
    var taskItemPreviewData: List<TaskItemPreviewData>,
    var task: List<TaskItem>
)