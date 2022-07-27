package ru.telecor.gm.mobile.droid.ui.garbageload.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_garbage_load.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.ContainerLoadLevel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.garbageload.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 05.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
class ContainerViewHolder(
    override val containerView: View,
    private val levelList: List<ContainerLoadLevel>,
    //private val onLoadLevelSelected: (StatusTaskExtended, ContainerLoadLevel) -> Unit
    private val onLoadLevelClicked: (StatusTaskExtended) -> Unit
) : BaseViewHolder<StatusTaskExtended>(containerView) {

    override fun bind(entity: StatusTaskExtended) {
        tvContainerTypeName.text = entity.containerType.name
//        actvGarbageLoadLevelDropdownMenu.setAdapter(
//            ArrayAdapter(
//                containerView.context,
//                R.layout.dropdown_item_simple_text,
//                levelList.map { it -> it.title }
//            )
//        )
//        actvGarbageLoadLevelDropdownMenu.setOnItemClickListener { _, _, position, _ ->
//            onLoadLevelSelected(entity, levelList[position])
//        }

        actvGarbageLoadLevelDropdownMenu.setOnClickListener { onLoadLevelClicked(entity) }
        actvGarbageLoadLevelDropdownMenu.text?.clear()
        ivIndicator.visible(true)
        entity.containerStatus?.volumePercent?.let {
            actvGarbageLoadLevelDropdownMenu.setText(levelList.find { containerLoadLevel -> containerLoadLevel.value == it }?.title)
            ivIndicator.visible(false)
        }
        entity.containerStatus?.containerFailureReason?.let {
            actvGarbageLoadLevelDropdownMenu.setText(it.name)
            ivIndicator.visible(false)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            levelList: List<ContainerLoadLevel>,
            onLoadLevelClicked: (StatusTaskExtended) -> Unit
        ) =
            ContainerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_garbage_load, parent, false), levelList,
                onLoadLevelClicked
            )
    }
}
