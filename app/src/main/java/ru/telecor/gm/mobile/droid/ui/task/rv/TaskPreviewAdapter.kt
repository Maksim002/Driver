package ru.telecor.gm.mobile.droid.ui.task.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.task.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 11.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class TaskPreviewAdapter : BaseAdapter<TaskPreviewViewHolder, TaskItemPreviewData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskPreviewViewHolder =
        TaskPreviewViewHolder.create(parent)
}
