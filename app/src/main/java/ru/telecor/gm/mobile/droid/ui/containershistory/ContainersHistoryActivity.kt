package ru.telecor.gm.mobile.droid.ui.containershistory

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_containers_history.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.presentation.containershistory.ContainersHistoryPresenter
import ru.telecor.gm.mobile.droid.presentation.containershistory.ContainersHistoryView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.containershistory.rv.ContainersHistoryAdapter
import toothpick.Toothpick

class ContainersHistoryActivity : BaseActivity(), ContainersHistoryView {

    override val layoutResId = R.layout.fragment_containers_history

    val recyclerAdapter = ContainersHistoryAdapter()

    @InjectPresenter
    lateinit var presenter: ContainersHistoryPresenter

    @ProvidePresenter
    fun providePresenter(): ContainersHistoryPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ContainersHistoryPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvContainersHistory.adapter = recyclerAdapter

        btnBack.setOnClickListener { onBackPressed() }
    }

    override fun setHistoryList(list: List<Pair<CarryContainerHistoryItem, Boolean>>) {
        recyclerAdapter.setList(list.reversed())
    }
}
