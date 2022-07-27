package ru.telecor.gm.mobile.droid.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun millisecondsTodDate(timestamp: String): String? {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        formatter.format(Date(timestamp.toLong()))
    }catch (e: Exception){
        e.printStackTrace()
        null
    }
}

fun getCalculatedDate(dateFormat: String?, days: Int): String? {
    val cal = Calendar.getInstance()
    val s = SimpleDateFormat(dateFormat)
    cal.add(Calendar.DAY_OF_YEAR, days)
    return s.format(Date(cal.timeInMillis))
}

fun milliseconds(date: String?): Long {
    //String date_ = date;
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    try {
        val mDate = sdf.parse(date)
        val timeInMilliseconds = mDate.time
        return timeInMilliseconds
    } catch (e: ParseException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    return 0
}

fun millisecondsDate(currentTime: Long):String {
    val timeZoneDate = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeZoneDate.format(Date(currentTime))
}

fun timeDate(currentTime: Long): String{
    val timeZoneDate = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
    return timeZoneDate.format(Date(currentTime))
}

fun dateTime(): String{
    val currentDate = SimpleDateFormat("dd.MM.yyyy ", Locale.getDefault()).format(Date())
    val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    return currentDate + currentTime
}



fun getDateTime(epoc: Long): String{
    try {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(epoc)
    } catch (e: Exception) {
        return ""
    }
}

fun dataTimePhoto(): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(Date())
}