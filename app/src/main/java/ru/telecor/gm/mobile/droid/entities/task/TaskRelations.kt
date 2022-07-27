package ru.telecor.gm.mobile.droid.entities.task

import androidx.room.Embedded
import androidx.room.Relation
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult

data class TaskRelations(

    @Embedded
    val task: TaskExtended,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val taskProcessingResult: TaskProcessingResult?,
    var windowVisibility: Boolean? = true,
    var isEdit: Boolean? = false,

)