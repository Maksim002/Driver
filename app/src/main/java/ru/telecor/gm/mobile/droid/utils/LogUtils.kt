package ru.telecor.gm.mobile.droid.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


object LogUtils {
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())

    @SuppressLint("LogNotTimber")
    fun info(tag: String, info: String?) {
        Log.i(tag, "[$currentDate] $info")
    }

    @SuppressLint("LogNotTimber")
    fun error(tag: String, error: String?) {
        Log.e(tag, "[${sdf.format(Date())}] $error")
    }

    @SuppressLint("LogNotTimber")
    fun error(tag: String, error: String?, e: Exception?) {
        Log.e(tag, "[${sdf.format(Date())}] $error", e)
    }

    @SuppressLint("LogNotTimber")
    fun warning(tag: String, warning: String?) {
        Log.w(tag, "[$currentDate] $warning")
    }
}