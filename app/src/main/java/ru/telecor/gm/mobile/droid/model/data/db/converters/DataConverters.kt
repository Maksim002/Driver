package ru.telecor.gm.mobile.droid.model.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.processing.ProcessingStatusType
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.entities.task.ContainerAction
import ru.telecor.gm.mobile.droid.entities.task.Priority

class DataConverters {

    private val gson = Gson()

    private inline fun <reified T> Gson.fromJson(json: String?) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)


    private inline fun <reified T> Any.toJson(): String =
        gson.toJson(this, object : TypeToken<T>() {}.type)


    @TypeConverter
    fun fromStand(value: Stand?): String? = value?.toJson<Stand>()
    @TypeConverter
    fun toStand(value: String?): Stand? =  gson.fromJson(value)

    @TypeConverter
    fun fromContainerAction(value: ContainerAction): String = value.toJson<ContainerAction>()
    @TypeConverter
    fun toContainerAction(value: String?): ContainerAction = gson.fromJson(value)

    @TypeConverter
    fun fromPriority(value: Priority?): String? = value?.toJson<Priority>()
    @TypeConverter
    fun toPriority(value: String?): Priority? =  gson.fromJson(value)

    @TypeConverter
    fun fromVisitPoint(value: VisitPoint?): String? = value?.toJson<VisitPoint>()
    @TypeConverter
    fun toVisitPoint(value: String?): VisitPoint? =  gson.fromJson(value)

    @TypeConverter
    fun fromStatusType(value: ProcessingStatusType): String = value.toJson<ProcessingStatusType>()
    @TypeConverter
    fun toStatusType(value: String): ProcessingStatusType =  gson.fromJson(value)

    @TypeConverter
    fun fromFailureReason(value: TaskFailureReason?): String? = value?.toJson<TaskFailureReason>()
    @TypeConverter
    fun toFailureReason(value: String?): TaskFailureReason?=  gson.fromJson(value)

}