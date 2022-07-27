package ru.telecor.gm.mobile.droid.entities

import ru.telecor.gm.mobile.droid.entities.task.TaskItemExtended

data class CountableContainersResult(
    val taskItem: TaskItemExtended,
    val numberOfDoneContainers: Int,
    val numberOfOverweightContainers: Int
)
