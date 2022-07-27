package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_garbage_load_count.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.CountableContainersResult
import ru.telecor.gm.mobile.droid.entities.task.TaskItemExtended
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

class CountableContainerViewHolder(
    override val containerView: View,
    private val onEditChange: (CountableContainersResult) -> Unit
) :
    BaseViewHolder<TaskItemExtended>(containerView) {

    override fun bind(entity: TaskItemExtended) {
        tvContainerTypeName.text = entity.containerType.name
        tvPlanContainersCount.text = "План: " + entity.planCount

        ivIndicator.visible(true)
        val doneInfo = entity.statuses.mapNotNull { item -> item.containerStatus }
        if (doneInfo.isNotEmpty()) {
            tietGarbageCount.setText(doneInfo.filter { item -> item.containerFailureReason == null }.size.toString())
            tietGarbageOverweightCount.setText(doneInfo.filter { item -> item.volumePercent == 1.25 }.size.toString())
            ivIndicator.visible(false)
        }

        tietGarbageCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (tietGarbageCount.text.toString().toIntOrNull() ?: 0 > entity.planCount) {
                    tietGarbageCount.error = "Слишком большое число. Исправьте данные"
                }
                sendData(entity)
            }
        })

        tietGarbageOverweightCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (tietGarbageOverweightCount.text.toString()
                        .toIntOrNull() ?: 0 > tietGarbageCount.text.toString().toIntOrNull() ?: 0
                ) {
                    tietGarbageOverweightCount.error = "Слишком большое число. Исправьте данные"
                }
                sendData(entity)
            }
        })
    }

    private fun sendData(entity: TaskItemExtended) {
        ivIndicator.visible(false)
        onEditChange(
            CountableContainersResult(
                entity,
                tietGarbageCount.text.toString().toIntOrNull() ?: 0,
                tietGarbageOverweightCount.text.toString().toIntOrNull() ?: 0
            )
        )
    }

    companion object {

        fun create(parent: ViewGroup, onEditChange: (CountableContainersResult) -> Unit) =
            CountableContainerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_garbage_load_count, parent, false), onEditChange
            )
    }
}
