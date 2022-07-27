package ru.telecor.gm.mobile.droid.ui.splash

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_splash.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.splash.SplashPresenter
import ru.telecor.gm.mobile.droid.presentation.splash.SplashView
import ru.telecor.gm.mobile.droid.ui.login.LoginActivity
import ru.telecor.gm.mobile.droid.ui.main.MainActivity
import toothpick.Toothpick
import android.animation.AnimatorListenerAdapter
import android.view.View

class SplashActivity : MvpAppCompatActivity(), SplashView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo.animate()
            .translationY(logo.height.toFloat())
            .alpha(1f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    logo.visibility = View.VISIBLE
                }
            })
    }

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @ProvidePresenter
    fun providePresenter(): SplashPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(SplashPresenter::class.java)
    }

    override fun openMainScreen() {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        },2000)
    }

    override fun openLoginScreen() {
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
        },2000)
    }

    override fun setCurrentVersion(version: String) {
        splashVersionTxt.text = version
    }
}