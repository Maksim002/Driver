package ru.telecor.gm.mobile.droid.ui.dumping.fragment.unloading

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.ragment_cancellation_unloading_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import toothpick.Toothpick

class CancellationUnloadingSheetFragment(var listener: UnloadingListener) : BaseBottomSheetFragment(), CancellationView {
    override val layoutRes = R.layout.ragment_cancellation_unloading_sheet

    @InjectPresenter
    lateinit var presenter: CancellationPresenter

    @ProvidePresenter
    fun providePresenter(): CancellationPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(CancellationPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        cancelNoBtn.setOnClickListener {
            dismiss()
        }

        cancelYesBtn.setOnClickListener {
            listener.setOnClickListener()
            dismiss()
        }
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    interface UnloadingListener{
        fun setOnClickListener()
    }
}
