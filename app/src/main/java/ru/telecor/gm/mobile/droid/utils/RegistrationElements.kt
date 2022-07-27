package ru.telecor.gm.mobile.droid.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task

fun conect(context: Context, activity: Activity): Boolean{
    var value = true
    val msss = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!msss.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show()
        createLocationRequest(activity)
        value = false
    }else{
        value = true
    }
    return value
}

//GPS switch
fun createLocationRequest(activity: Activity){
    val locationRequest: LocationRequest = LocationRequest.create()
    locationRequest.interval = 10000
    locationRequest.fastestInterval = 5000
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(activity.applicationContext)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener(activity) {
        Toast.makeText(activity.applicationContext, "Gps already open", Toast.LENGTH_LONG).show()
    }
    task.addOnFailureListener(activity) { e ->
        if (e is ResolvableApiException) {
            try {
                e.startResolutionForResult(activity, 6)
            } catch (sendEx: IntentSender.SendIntentException) {
                sendEx.printStackTrace()
            }
        }
    }
}