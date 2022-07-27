package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.telecor.gm.mobile.droid.entities.CountableContainersResult
import ru.telecor.gm.mobile.droid.entities.task.TaskItemExtended
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter
import ru.telecor.gm.mobile.droid.ui.garbageload.rv.diffutil.CountableContainerDiffUtilCallback

class CountableContainerAdapter(private val onEditFinished: (CountableContainersResult) -> Unit) :
    BaseAdapter<CountableContainerViewHolder, TaskItemExtended>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountableContainerViewHolder = CountableContainerViewHolder.create(parent, onEditFinished)

    override fun setList(list: List<TaskItemExtended>) {
        val diffUtilCallback = CountableContainerDiffUtilCallback(dataSrc, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        dataSrc = list
        diffResult.dispatchUpdatesTo(this)
    }
}
