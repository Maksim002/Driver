package ru.telecor.gm.mobile.droid.ui.dumping.fragment.authorization

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_authorization_bottom_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import toothpick.Toothpick

class AuthorizationBottomSheetFragment() : BaseBottomSheetFragment(), AuthorizationView {

    override val layoutRes = R.layout.fragment_authorization_bottom_sheet

    @InjectPresenter
    lateinit var presenter: AuthorizationPresenter

    @ProvidePresenter
    fun providePresenter(): AuthorizationPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(AuthorizationPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    private fun initClick() {
        weightCancelBtn.setOnClickListener {
            dismiss()
        }

        weightSaveBtn.setOnClickListener {
            presenter.onPolygonWeightEntered(textWeight.toString())
        }
    }

    override fun clearWindow() {
        dismiss()
    }

    override fun showMessage(msg: String) {

    }
}