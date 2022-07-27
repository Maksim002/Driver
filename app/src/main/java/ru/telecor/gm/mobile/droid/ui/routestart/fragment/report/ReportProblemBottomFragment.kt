package ru.telecor.gm.mobile.droid.ui.routestart.fragment.report

import android.os.Build
import android.os.Bundle
import android.view.View
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import toothpick.Toothpick

class ReportProblemBottomFragment : BaseBottomSheetFragment(), ReportProblemView {
    override val layoutRes = R.layout.fragment_report_problem_sheet

    @InjectPresenter
    lateinit var presenter: ReportProblemBottomPresenter

    @ProvidePresenter
    fun providePresenter(): ReportProblemBottomPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ReportProblemBottomPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}
}
