package ru.telecor.gm.mobile.droid.utils.сomponent.collapsingBar

import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_garbage_load.*

object CollapsingBar {
    private var mut = MutableLiveData(true)

    fun setOnClick(
        bottom: View,
        fragment: Fragment,
        collapsing: View,
        appBar: AppBarLayout,
        message: View
    ) {
        bottom.setOnClickListener {
            if (collapsing.isVisible) {
                collapsing.isVisible = false
                slideDown(collapsing, message, 0)
                appBar.setExpanded(true)
                collapsing.scrollTo(0,0)
            } else {
                collapsing.isVisible = true
                message.isVisible = false
                slideDown(message, message, 1)
                slideUp(collapsing)
            }
            ViewCompat.setNestedScrollingEnabled(collapsing, collapsing.isVisible)
            mut.value = collapsing.isVisible
        }
        scrollBar(appBar, fragment)
    }

    private fun scrollBar(view: View, fragment: Fragment) {
        mut.observe(fragment, {
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            if (params.behavior == null)
                params.behavior = AppBarLayout.Behavior()
            val behaviour = params.behavior as AppBarLayout.Behavior
            behaviour.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return it
                }
            })
        })
    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            view.height.toFloat(),  // fromYDelta
            0F
        ) // toYDelta
        animate.duration = 300
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    private fun slideDown(view: View, message: View, id: Int) {
        val animate = TranslateAnimation(
            0F,  // fromXDelta
            0F,  // toXDelta
            0F,  // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 300
        animate.fillAfter = true
        if (id == 0) {
            animate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    slideUp(message)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        view.startAnimation(animate)
    }
}