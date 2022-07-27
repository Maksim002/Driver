package ru.telecor.gm.mobile.droid.ui.base.rv

import androidx.recyclerview.widget.RecyclerView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.base.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 11.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
abstract class BaseAdapter<T : BaseViewHolder<E>, E : Any>(
    protected var dataSrc: List<E> = emptyList()
) : RecyclerView.Adapter<T>() {

    override fun getItemCount(): Int = dataSrc.size

    override fun onBindViewHolder(holder: T, position: Int) = holder.bind(dataSrc[position])

    open fun setList(list: List<E>) {
        dataSrc = list
        this.notifyDataSetChanged()
    }

    fun updateWindow(list: List<E>){
        dataSrc = list
        this.notifyDataSetChanged()
    }

    fun addItem(entity: E) {
        dataSrc = dataSrc.plus(entity)
        this.notifyItemInserted(dataSrc.size - 1)
    }

    fun deleteItem(entity: E) {
        val index = dataSrc.indexOf(entity)
        if (index == -1) {
            return
        }
        dataSrc = dataSrc.minus(entity)
        this.notifyItemRemoved(index)
    }

    fun addAll(list: List<E>) {
        val startIndex = dataSrc.size
        dataSrc = dataSrc.plus(list)
        this.notifyItemRangeInserted(startIndex, dataSrc.size)
    }
}
