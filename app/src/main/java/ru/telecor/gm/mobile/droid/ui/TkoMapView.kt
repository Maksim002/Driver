package ru.telecor.gm.mobile.droid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 3/24/21
 * Copyright © 2020 TKOInform. All rights reserved.
 */
class TkoMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MapView(context, attrs, defStyleAttr) {

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN ->
//                this.parent.requestDisallowInterceptTouchEvent(true)
//            MotionEvent.ACTION_UP ->
//                this.parent.requestDisallowInterceptTouchEvent(false)
//        }
//
//        // Handle MapView's touch events.
//        super.onTouchEvent(event)
//        return true
//    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->
                this.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP ->
                this.parent.requestDisallowInterceptTouchEvent(false)
        }

        return super.dispatchTouchEvent(ev)
    }
}