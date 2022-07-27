package ru.telecor.gm.mobile.droid.ui.base.rv

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class GenericRecyclerAdapter<T>(var items: List<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastClickTime: Long = 0

    abstract fun bind(item: T, holder: ViewHolder)

    fun update(items: List<T>) {
        this.items = items
        notifyItemRangeChanged(items.size ,items.size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    fun getListPosition(position: Int): T {
        return items[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(items[position], holder as ViewHolder)
    }

    fun View.setOnClickListener(debounceTime: Long, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action()

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}