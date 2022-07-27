package ru.telecor.gm.mobile.droid.model.data.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import ru.telecor.gm.mobile.droid.entities.processing.StandResult
import ru.telecor.gm.mobile.droid.entities.task.TaskItem
import com.google.gson.JsonElement

import com.google.gson.JsonParser
import java.lang.Exception


class ListDataConverters {

    private val gson = Gson()

    private inline fun <reified T> fromJson(json: String?, cls: Class<T>?): List<T> {
        val list: MutableList<T> = ArrayList()
        try {
            val gson = Gson()
            val arry = JsonParser().parse(json).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }


    private inline fun <reified T> Any.toJson(): String =
        gson.toJson(this, object : TypeToken<List<T>>() {}.type)


    @TypeConverter
    fun fromTaskItems(value: List<TaskItem>): String = value.toJson<TaskItem>()
    @TypeConverter
    fun toTaskItems(value: String): List<TaskItem> = fromJson(value,TaskItem::class.java)

    @TypeConverter
    fun fromStandResults(value: List<StandResult> ?): String? = value?.toJson<StandResult>()
    @TypeConverter
    fun toStandResults(value: String?): List<StandResult>? =  if (value != null) fromJson(value,StandResult::class.java) else null

    @TypeConverter
    fun fromDetourPointProcessingResults(value: List<Any>?): String? = value?.toJson<Any>()
    @TypeConverter
    fun toDetourPointProcessingResults(value: String?): List<Any>? = if (value != null) fromJson(value,Any::class.java)else null


}