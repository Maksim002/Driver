package ru.telecor.gm.mobile.droid.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.telecor.gm.mobile.droid.utils.LoadingAlert
import moxy.MvpAppCompatActivity
import ru.telecor.gm.mobile.droid.extensions.showToast
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.base.BaseView
import ru.telecor.gm.mobile.droid.utils.getPositionNetwork

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.base
 *
 * Base MVP Activity class for the project. Contains a few features to use.
 *
 * Created by Artem Skopincev (aka sharpyx) 15.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
abstract class BaseActivity : MvpAppCompatActivity(), BaseView {

    var lastClickTime: Long = 0

    companion object {
        lateinit var alertDialog: LoadingAlert
        const val REQUEST_CODE_PERMISSIONS = 333
    }

    abstract val layoutResId: Int
    open var isPortrait: Boolean = false
    open var isFullscreen: Boolean = false

    private var permissionsForRequest: Pair<String, Pair<() -> Unit, () -> Unit>>? = null

    private lateinit var locationManager: LocationManager
    protected var isNetworkProviderOn = false
    protected var isGPSProviderOn = false

    private var locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location) {
            //вроде как здесь ничего не обязано быть. Мне просто нужно подписаться на геолокацию
        }

        override fun onProviderDisabled(provider: String) {
            //super приводит к падению приложения при включении/выключении геопозиции. Будьте аккуратны!
            try {
                updateLocationProvidersBooleans()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        override fun onProviderEnabled(provider: String) {
            subscribeToLocationChanges()
        }

        //Если не переопределить, вечно падает
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPositionNetwork(this)

        if (isPortrait) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (isFullscreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        if (layoutResId != 0) setContentView(layoutResId)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        subscribeToLocationChanges()
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToLocationChanges() {
        try {
            withPermission(Manifest.permission.ACCESS_FINE_LOCATION, {
                withPermission(Manifest.permission.ACCESS_COARSE_LOCATION, {
                    if (locationManager.allProviders.contains(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            100L,
                            10F,
                            locationListener
                        )
                    }
                    if (locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            100L,
                            10F,
                            locationListener
                        )
                    }
                }, {})
            }, {})
            updateLocationProvidersBooleans()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    open fun updateLocationProvidersBooleans() {
        isNetworkProviderOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        isGPSProviderOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun showMessage(msg: String) {
        showToast(msg)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (permissionsForRequest != null && !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val granted = permissionsForRequest?.second?.first
                if (granted != null) {
                    granted()
                }
            } else {
                val denied = permissionsForRequest?.second?.second
                if (denied != null) {
                    denied()
                }
            }
        }
    }

    fun withPermission(permission: String, granted: () -> Unit, denied: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            granted()
        } else {
            permissionsForRequest = Pair(permission, Pair(granted, denied))
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    fun View.setOnClickListener(debounceTime: Long, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }

    protected fun showViews(vararg views: View, show: Boolean, hideType: Int = View.GONE) {
        views.forEach { it.visible(show, hideType) }
    }
}