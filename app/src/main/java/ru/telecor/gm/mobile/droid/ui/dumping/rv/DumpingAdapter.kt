package ru.telecor.gm.mobile.droid.ui.dumping.rv

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_dumping_recycler.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.utils.millisecondsDate

class DumpingAdapter(item: ArrayList<GetListCouponsModel> = arrayListOf()): GenericRecyclerAdapter<GetListCouponsModel>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_dumping_recycler)
    }

    override fun bind(item: GetListCouponsModel, holder: ViewHolder) = with(holder.itemView) {
        item.unloading!!.forEach {
            if (it.visitPoint!!.equipmentStatus != "NOT_EQUIPED"){
                if (it.garbageType != null){
                    placeText.text = it.garbageType!!.name
                }

                if (it.number != null){
                    couponNumberIdTxt.text = "Талон № " + it.number.toString()
                }
                if (it.driver != null){
                    driverTxt.text = it.driver
                }
                if (it.vehicle != null){
                    vehicleTxt.text = it.vehicle
                }

                if (it.weighingTime != null){
                    entryText.setTextColor(ContextCompat.getColor(context, R.color.color_text_w))
                    entryImage.setColorFilter(ContextCompat.getColor(context, R.color.color_text_w))
                    entryText.text = "Въезд: " + millisecondsDate(it.weighingTime!!.toLong())
                }

                if (it.arriveTime != null){
                    exitText.setTextColor(ContextCompat.getColor(context, R.color.color_text_w))
                    exitImage.setColorFilter(ContextCompat.getColor(context, R.color.color_text_w))
                    exitText.text = "Выезд: " + millisecondsDate(it.arriveTime!!.toLong())
                }

                if (it.netWeight != null){
                    consDumpingWeight.background = resources.getDrawable(R.drawable.circle_background_text_rounding_blue, null)
                    netWeightText.setTextColor(ContextCompat.getColor(context, R.color.gmm_white))
                    dupNetWeight.setTextColor(ContextCompat.getColor(context, R.color.gmm_white))
                    weightIm.setColorFilter(ContextCompat.getColor(context, R.color.gmm_white), PorterDuff.Mode.MULTIPLY)
                    netWeightText.text = it.netWeight.toString()
                }

                if (it.primaryWeight != null){
                    weighingIText.background = resources.getDrawable(R.drawable.circle_background_text_rounding_grin, null)
                    weighingIText.setTextColor(ContextCompat.getColor(context, R.color.gmm_white))
                    weighingIText.text = it.primaryWeight.toString() + " кг"
                }

                if (it.secondaryWeight != null){
                    weighingIIText.background = resources.getDrawable(R.drawable.circle_background_text_rounding_grin, null)
                    weighingIIText.setTextColor(ContextCompat.getColor(context, R.color.gmm_white))
                    weighingIIText.text = it.secondaryWeight.toString() + " кг"
                }
            }
        }
    }
}