package ru.telecor.gm.mobile.droid.presentation.base

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.presentation.base
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 15.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
interface BaseView : MvpView {
    @Skip
    fun showMessage(msg: String)
}