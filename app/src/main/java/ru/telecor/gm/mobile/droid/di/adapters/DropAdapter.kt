package ru.telecor.gm.mobile.droid.di.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.LoaderInfo

class DropAdapter(var listener: DropHolder.Listener, var item: ArrayList<LoaderInfo> = arrayListOf()): RecyclerView.Adapter<DropHolder>() {

    fun update(list: ArrayList<LoaderInfo> = arrayListOf()){
        item = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropHolder {
        return DropHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_riciler, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: DropHolder, position: Int) {
       holder.bond(item[holder.adapterPosition], holder)
    }

    fun filter(sequence: CharSequence? = null, list: ArrayList<LoaderInfo>? = null): ArrayList<LoaderInfo> {
        val temp = ArrayList<LoaderInfo>()
        for (s in list!!) {
            if ((s.firstName + s.lastName).toLowerCase().contains(sequence!!)) {
                temp.add(s)
            }

            if (s.employeeId.toLowerCase().contains(sequence)) {
                temp.add(s)
            }
        }
        update(temp)
        return temp
    }
}