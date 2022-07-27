package ru.telecor.gm.mobile.droid.ui.dumping.fragment.successful

import android.os.Build
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_successful_receipt_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import toothpick.Toothpick

class SuccessfulReceiptSheetFragment(var listener: ListenerSuccessfulReceipt) : BaseBottomSheetFragment(), SuccessfulReceiptView{

    override val layoutRes = R.layout.fragment_successful_receipt_sheet

    @InjectPresenter
    lateinit var presenter: SuccessfulReceiptPresenter

    @ProvidePresenter
    fun providePresenter(): SuccessfulReceiptPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(SuccessfulReceiptPresenter::class.java)
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
        continueRouteBtn.setOnClickListener {
            listener.setonClickListener()
            dismiss()
        }
    }

    override fun showMessage(msg: String) {

    }

    interface ListenerSuccessfulReceipt{
        fun setonClickListener()
    }
}