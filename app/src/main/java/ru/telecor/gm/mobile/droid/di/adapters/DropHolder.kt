package ru.telecor.gm.mobile.droid.di.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_riciler.view.*
import kotlinx.android.synthetic.main.item_riciler.view.imageProfile
import kotlinx.android.synthetic.main.item_riciler.view.titleTxt
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.utils.PhotoUtils.stringToObject

class DropHolder(iteView: View, var listener: Listener): RecyclerView.ViewHolder(iteView) {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bond(item: LoaderInfo, holder: DropHolder) = with(holder.itemView) {
        val first = item.firstName
        val last = item.lastName
        if (item.middleName != null){
            val middle = item.middleName
            titleTxt.text = "$first $last $middle"
        }else{
            titleTxt.text = "$first $last"
        }
        if (item.photo.toString() != "null"){
            imageProfile.setImageBitmap(stringToObject(item.photo!!.content.toString()))
        }else{
            imageProfile.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile, null))
        }
        descriptionTxt.text = item.employeeId

        nameLay.setOnClickListener {
            listener.setOnClickListener(item)
        }
    }

    interface Listener{
        fun setOnClickListener(item: LoaderInfo)
    }
}

