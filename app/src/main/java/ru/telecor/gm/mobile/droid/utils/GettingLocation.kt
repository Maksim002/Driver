package ru.telecor.gm.mobile.droid.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.model.system.LocationProvider
import java.util.*


private lateinit var mLocationManagerNetwork: LocationManager
private lateinit var mLocationListenerNetwork: LocationListener
//var partOne = MutableLiveData<String>()
//var partTwo = MutableLiveData<String>()
//var latitude = MutableLiveData<Float>()
//var longitude = MutableLiveData<Float>()

@SuppressLint("ObsoleteSdkInt", "NewApi")
fun getPositionNetwork(activity: Activity) {
    try {
        mLocationManagerNetwork =
            activity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        mLocationListenerNetwork = object : LocationListener {
            override fun onLocationChanged(p0: Location) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onProviderEnabled(provider: String) {}
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    activity.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                if (requestLocationPermission(activity)) {
                    mLocationManagerNetwork.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5,
                        0f,
                        mLocationListenerNetwork
                    )
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun requestLocationPermission(activity: Activity): Boolean {
    var value = true
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.GPS_permissions).setCancelable(false)
            .setPositiveButton(R.string.btn_yes) { dialog, which ->
                value = false
                dialog.cancel()
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }.show()
    }
    return value
}