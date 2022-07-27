package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import kotlinx.android.synthetic.main.item_garbage_container.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended

class GarbageContainerAdapter(var listener: GarbageListenerState? = null, var item: ArrayList<StatusTaskExtended> = arrayListOf()): GenericRecyclerAdapter<StatusTaskExtended>(item) {

    private var isCheck = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_garbage_container)
    }

    override fun bind(item: StatusTaskExtended, holder: ViewHolder) = with(holder.itemView) {
        containerCheck.isChecked = isCheck
        containerTxt.text = item.containerType.name
        garbageStatus.text = item.containerAction

        if (item.containerStatus != null){
            if (item.containerStatus.containerFailureReason != null){
                percentageFillingImageM.isVisible = true
                statusDescription.isVisible = true

                percentageFilling.isVisible = false
                percentageFillingImage.isVisible = false
                statusDescription.text = item.containerStatus.containerFailureReason.name
                garbageTxt.text = item.containerGroups?.name
                statusDescription.setTextColor(ContextCompat.getColor(context, R.color.black_to_red_color))
                mark.background = resources.getDrawable(R.drawable.circle_red_mark_background_layout, null)
                containerImage.setColorFilter(ContextCompat.getColor(context, R.color.black_to_red_color))
            }else{
                when (item.rule) {
                    "SUCCESS_FAIL" -> {
                        percentageFillingImage.isVisible = true

                        percentageFillingImageM.isVisible = false
                        percentageFilling.isVisible = false
                        statusDescription.isVisible = false
                        weightText.isVisible = false
                    }
                    "SUCCESS_FAIL_MANUAL_VOLUME" -> {
                        percentageFillingImage.isVisible = true
                        weightText.isVisible = true

                        weightText.text = item.containerStatus.volumeAct!!.toDouble().toString()

                        percentageFillingImageM.isVisible = false
                        percentageFilling.isVisible = false
                        statusDescription.isVisible = false
                    }
                    "SUCCESS_FAIL_MANUAL_COUNT_WEIGHT" -> {
                        percentageFillingImage.isVisible = true
                        kilogramText.isVisible = true

                        kilogramText.text = item.containerStatus.volumeAct!!.toDouble().toString() + " КГ"

                        percentageFillingImageM.isVisible = false
                        percentageFilling.isVisible = false
                        statusDescription.isVisible = false
                        weightText.isVisible = false
                    }
                    "SUCCESS_FAIL_FILLING" -> {
                        percentageFilling.isVisible = true
                        statusDescription.isVisible = true

                        percentageFillingImageM.isVisible = false
                        percentageFillingImage.isVisible = false
                        weightText.isVisible = false
                        when(item.containerStatus.volumePercent){
                            0.0 ->{
                                getParameters(R.color.empty_zero, holder, "0", "ПУСТОЙ")
                            }
                            0.25 ->{
                                getParameters(R.color.empty_one, holder, "25", "ЧЕТВЕРТЬ")
                            }
                            0.5 ->{
                                getParameters(R.color.empty_two, holder, "50", "НАПОЛОВИНУ")
                            }
                            0.75 ->{
                                getParameters(R.color.empty_three, holder, "75", "НЕПОЛНЫЙ")
                            }
                            1.0 ->{
                                getParameters(R.color.empty_four, holder, "100", "ПОЛНЫЙ")
                            }
                            1.25 ->{
                                getParameters(R.color.empty_five, holder, "125", "ПЕРЕПОЛНЕН")
                            }
                        }
                    }
                }
                if (item.rule != "SUCCESS_FAIL_FILLING"){
                    containerImage.setColorFilter(ContextCompat.getColor(context, R.color.grey_color ))
                }
                mark.background = resources.getDrawable(R.drawable.circle_grin_mark_background_layout, null)
            }
            percentageFillingImage.setColorFilter(ContextCompat.getColor(context, R.color.gmm_white));
        }

        containerCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                listener!!.setOnClickListener(item)
            }else{
                if (!isChecked){
                    listener!!.setOnClickListenerCheck(isChecked)
                }
            }
        }

        this.setOnClickListener {
            containerCheck.isChecked = true
            listener!!.setOnClickListener(item)
        }
    }

    private fun getParameters(color: Int, holder: ViewHolder, text: String, textDis: String) = with(holder.itemView){
        statusDescription.setTextColor(ContextCompat.getColor(context, color))
        containerImage.setColorFilter(ContextCompat.getColor(context, color))
        statusDescription.text = textDis
        percentageFilling.text = "$text%"
    }

    fun setCheck(check: Boolean = false){
        isCheck = check
    }

    interface GarbageListenerState{
        fun setOnClickListener(item: StatusTaskExtended)
        fun setOnClickListenerCheck(isCheck: Boolean)
    }
}