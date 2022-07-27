package ru.telecor.gm.mobile.droid.ui.base.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.base.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 11.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
abstract class BaseViewHolder<T>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    abstract fun bind(entity: T)
}
