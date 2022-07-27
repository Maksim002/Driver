package ru.telecor.gm.mobile.droid.ui.garbageload.fragment.message

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.viewpager.widget.ViewPager
import ru.telecor.gm.mobile.droid.ui.garbageload.fragment.fragments.ProblemFragment
import ru.telecor.gm.mobile.droid.ui.garbageload.fragment.fragments.SuccessfullyFragment
import kotlinx.android.synthetic.main.fragment_selecting_export.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.presentation.garbageload.presenterMessage.SelectingExportBottomView
import ru.telecor.gm.mobile.droid.presentation.garbageload.presenterMessage.SelectingExportPresenter
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.garbageload.pager.SelectionPagerAdapter
import toothpick.Toothpick
import android.widget.LinearLayout
import okhttp3.internal.http2.Http2Reader

class SelectingExportItemFragment(
    var listener: SuccessfullyFragment.SuccessfullyListener,
    var listenerProblem: ProblemFragment.ProblemListener,
    var item: StatusTaskExtended? = null,
    private val localFailureReasonsCache: List<ContainerFailureReason>? = null,
    var nameStatus: String
) :
    BaseBottomSheetFragment(), SelectingExportBottomView {

    private var numberBar = 0
    private lateinit var params: LinearLayout.LayoutParams

    override val layoutRes = R.layout.fragment_selecting_export

    @InjectPresenter
    lateinit var presenter: SelectingExportPresenter

    @ProvidePresenter
    fun providePresenter(): SelectingExportPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(SelectingExportPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        params = linearLayout.layoutParams as LinearLayout.LayoutParams
        initPager()
        orinDevice()
    }

    private fun orinDevice() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        } else {
            params.height = 1100
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height = 1000
        }
    }

    private fun initPager() {
        val adapter = SelectionPagerAdapter(childFragmentManager)
        adapter.addFragment(ProblemFragment(object : ProblemFragment.ProblemListener {
            override fun setOnClickListenerProgress(value: Int) {
                listenerProblem.setOnClickListenerProgress(value)
            }

            override fun setClickCancellation(boolean: Boolean) {
                listenerProblem.setClickCancellation(boolean)
            }
        }, localFailureReasonsCache!!, this, item), "ПРОБЛЕМА")

        adapter.addFragment(SuccessfullyFragment(object :
            SuccessfullyFragment.SuccessfullyListener {
            override fun setOnClickListenerProgress(value: Double) {
                listener.setOnClickListenerProgress(value)
            }

            override fun setClickCancellation(boolean: Boolean) {
                listener.setClickCancellation(boolean)
            }
        }, bottomSheetDialogFragment = this, item = item), "УСПЕШНО")

        profilePager.adapter = adapter

        numButtonOne.setOnClickListener {
            profilePager.currentItem = 0
        }

        numButtonTwo.setOnClickListener {
            profilePager.currentItem = 1
        }

        profile_bar_zero.visibility = View.VISIBLE
        profile_bar_one.visibility = View.GONE

        profilePager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                numberBar = position
            }

            override fun onPageSelected(position: Int) {
                val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                if (position != 1) {
                    profile_bar_zero.startAnimation(fadeIn)
                    profile_bar_one.startAnimation(fadeOut)
                    profile_bar_zero.visibility = View.VISIBLE
                    profile_bar_one.visibility = View.GONE
                } else {
                    profile_bar_one.startAnimation(fadeIn)
                    profile_bar_zero.startAnimation(fadeOut)
                    profile_bar_one.visibility = View.VISIBLE
                    profile_bar_zero.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        if (nameStatus != "all"){
            if (item?.containerStatus?.containerFailureReason == null) {
                pagerStatus()
            }
        }else{
            pagerStatus()
        }
    }

    fun pagerStatus(){
        Handler().postDelayed({
            profilePager.currentItem = 1
        }, 20)
    }

    override fun getTheme(): Int {
        return if (Build.VERSION.SDK_INT > 22) {
            R.style.AppBottomSheetDialogTheme
        } else {
            0
        }
    }
}