package ru.telecor.gm.mobile.droid.ui.settings

import moxy.presenter.InjectPresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.presentation.settings.SettingsPresenter
import ru.telecor.gm.mobile.droid.presentation.settings.SettingsView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity

class SettingsActivity : BaseActivity(), SettingsView {

    override val layoutResId = R.layout.activity_settings

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

}
