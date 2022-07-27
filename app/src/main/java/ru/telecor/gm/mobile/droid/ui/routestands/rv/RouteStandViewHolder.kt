package ru.telecor.gm.mobile.droid.ui.routestands.rv

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.item_task_new.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.VisitPointType
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder
import ru.telecor.gm.mobile.droid.ui.routestands.fragment.rv.CansGarbageAdapter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.ui.routestands.rv
 *
 *
 *
 * Created by Emil Zamaldinov (aka piligrim) 03.08.2020
 * Copyright © 2020 TKO-StandRm. All rights reserved.
 */
class RouteStandViewHolder(
    override val containerView: View,
    private val viewType: Int,
    private val clickLambda: (TaskRelations) -> Unit,
    private val routeInfo: RouteInfo,
    val getListCouponsModel: List<GetListCouponsModel>
) : BaseViewHolder<TaskRelations>(containerView) {

    override fun bind(entity: TaskRelations) {

        layUnloading.setOnClickListener { clickLambda(entity) }

        when (entity.task.statusType) {
            StatusType.NEW -> {

                if (entity.task.isCurrent) {

                    layUnloading.background = getIcon(
                        entity.task.isCurrent,
                        ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background_accent),
                        ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background))

                    val param = divider.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0, 3, 0, 3)
                    divider.layoutParams = param

                } else {
                    layUnloading.background = ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background)
                }

                if (entity.task.visitPoint?.pointType?.name != null) {
                    getVisitPointIcon(entity.task)
                } else {
                    timelineNew.setImageDrawable(
                        getIcon(
                            entity.task.isCurrent,
                            getDrawable(itemView.context, R.drawable.ic_new_task),
                            getDrawable(itemView.context, R.drawable.ic_plus)
                        )
                    )
                    unloadingLay.isVisible = true
                    unloadingLayNew.isVisible = false
                    parkingSpaceLay.isVisible = false
                }

                if (entity.isEdit != null) {
                    if (entity.isEdit == true) {
                        timelineNew.setImageDrawable(
                            getIcon(
                                entity.task.isCurrent,
                                getDrawable(itemView.context, R.drawable.ic_current_edit),
                                getDrawable(itemView.context, R.drawable.ic_edit)
                            )
                        )

                        // set margin to view
                        val param = divider.layoutParams as ViewGroup.MarginLayoutParams
                        param.setMargins(0, 3, 0, 3)
                        divider.layoutParams = param
                    }

                }
            }

            StatusType.SUCCESS -> {

                layUnloading.background = ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background)

                if (entity.task.visitPoint?.pointType?.name != null) {
                    getVisitPointIcon(entity.task)
                } else {
                    timelineNew.setImageDrawable(
                        getIcon(
                            entity.task.isCurrent,
                            getDrawable(itemView.context, R.drawable.ic_check_mark),
                            getDrawable(itemView.context, R.drawable.ic_check_mark)
                        )
                    )
                    unloadingLay.isVisible = true
                    unloadingLayNew.isVisible = false
                    parkingSpaceLay.isVisible = false
                }
            }

            StatusType.PARTIALLY -> {

                layUnloading.background = ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background)

                if (entity.task.visitPoint?.pointType?.name != null) {
                    getVisitPointIcon(entity.task)
                } else
                    timelineNew.setImageDrawable(
                        getIcon(
                            entity.task.isCurrent,
                            getDrawable(itemView.context, R.drawable.ic_oreng_marker),
                            getDrawable(itemView.context, R.drawable.ic_oreng_marker)
                        )
                    )
                unloadingLay.isVisible = true
                unloadingLayNew.isVisible = false
                parkingSpaceLay.isVisible = false
            }

            StatusType.FAIL -> {

                layUnloading.background = ActivityCompat.getDrawable(itemView.context, R.drawable.shadow_background)

                if (entity.task.visitPoint?.pointType?.name != null) {
                    getVisitPointIcon(entity.task)
                } else
                    timelineNew.setImageDrawable(
                        getIcon(
                            entity.task.isCurrent,
                            getDrawable(itemView.context, R.drawable.ic_non_pickup),
                            getDrawable(itemView.context, R.drawable.ic_non_pickup)
                        )
                    )
                unloadingLay.isVisible = true
                unloadingLayNew.isVisible = false
                parkingSpaceLay.isVisible = false
            }
        }

        tvIsHardRoute.text = "Приоритет не задан"

        tvIsHardRoute.background =
            getDrawable(tvIsHardRoute.context, R.color.gmm_white)
        tvIsHardRoute.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.gray2
            )
        )

        layUnloading.background = ActivityCompat.getDrawable(
            itemView.context,
            R.drawable.shadow_background
        )

        if (entity.task.isCurrent && entity.task.statusType == StatusType.NEW) {
            layUnloading.background = ActivityCompat.getDrawable(
                itemView.context,
                R.drawable.shadow_background_accent
            )
        }

        if (entity.task.statusType == StatusType.NEW) {

            progAndFactTimeText.text = "Прогноз"
            unloadingFactTime.text = "Прогноз"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                progAndFactTimeText.setTextColor(
                    itemView.resources.getColor(
                        R.color.info,
                        null
                    )
                )

                unloadingFactTime.setTextColor(
                    itemView.resources.getColor(
                        R.color.info,
                        null
                    )
                )
                actualTimeText.setTextColor(
                    itemView.resources.getColor(
                        R.color.info,
                        null
                    )
                )
                actualTimeText.text =
                    SimpleDateFormat("HH:mm").format(Date(entity.task.planTimeStart))
                tvFactTimeR.text = SimpleDateFormat("HH:mm").format(Date(entity.task.planTimeStart))
                tvFactTimeR.setTextColor(itemView.resources.getColor(R.color.info, null))

            }

        } else {
            progAndFactTimeText.text = "Факт"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                progAndFactTimeText.setTextColor(
                    itemView.resources.getColor(
                        R.color.green_text,
                        null
                    )
                )
                tvFactTimeR.text = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date(entity.task.changeTime))
                tvFactTimeR.setTextColor(itemView.resources.getColor(R.color.green_text, null))

            }
        }
        cansGarbageRes.isVisible = true

        //new
        if (entity.task.contactPhone.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                item_contact_avatar.imageTintList =
                    itemView.context.getColorStateList(R.color.gray2)
            }
        }
        if (entity.task.comment.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                commentImage.backgroundTintList = itemView.context.getColorStateList(R.color.gray2)
                commentTextUser.setTextColor(itemView.context.getColorStateList(R.color.gray2))
                commentTextUser.text = ""
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                commentTextUser.text = "1"
                commentImage.backgroundTintList = itemView.context.getColorStateList(R.color.info)
                commentTextUser.setTextColor(itemView.context.getColorStateList(R.color.info))
            }
        }


        if (entity.windowVisibility != null) {
            cansGarbageRes.isVisible = entity.windowVisibility!!
            showInternetConnection(cansGarbageRes.isVisible)
        }

        tvTaskAddress.text = entity.task.stand?.address ?: entity.task.visitPoint?.name ?: ""

        tvPreferredTime.text =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date(entity.task.preferredTimeStart)).plus(
                "-" + SimpleDateFormat(
                    "HH:mm", Locale.getDefault()
                ).format(Date(entity.task.preferredTimeEnd))
            )

        if (entity.task.priority?.name.isNullOrEmpty()) {
            tvIsHardRoute.text = "Приоритет не задан"
            imageHardRoute.setImageDrawable(
                getDrawable(
                    itemView.context,
                    R.drawable.ic_circle_ellipse
                )
            )
        } else {
            val priority = entity.task.priority?.name
            tvIsHardRoute.text = priority.plus(". " + (entity.task.stand?.comment ?: ""))

            imageHardRoute.setImageDrawable(
                getDrawable(itemView.context, R.drawable.ic_warning_new))

            tvIsHardRoute.background =
                getDrawable(tvIsHardRoute.context, R.drawable.circle_background_for_textview)
            tvIsHardRoute.setTextColor(ContextCompat.getColor(itemView.context, R.color.black_color))

        }

        val myAdapter = CansGarbageAdapter { clickLambda(entity) }
        val item = adapterInfo(entity).sortedByDescending { it.supportedGarbageType.toString() }

        if (item.isNotEmpty()) {
            myAdapter.update(item)
            cansGarbageRes.adapter = myAdapter
        } else {
            cansGarbageRes.isVisible = false
        }


    }

    // parking
    private fun getVisitPointIcon(entity: ru.telecor.gm.mobile.droid.entities.db.TaskExtended) {
        when (entity.visitPoint?.pointType?.name) {

            VisitPointType.Type.Parking -> {

                parkingSpaceText.text = entity.visitPoint.name

                if (entity.isCurrent) {

                    timelineParkingSpace.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_parking_current
                        )
                    )
                    parkingContainer.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background_accent
                    )

                    parkingFactTimeText.text = "Прогноз"

                    tvFactTime.text =
                        SimpleDateFormat("HH:mm").format(Date(entity.planTimeStart))
                    parkingFactTimeText.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.info
                        )
                    )

                    tvFactTime.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.info
                        )
                    )
                } else {

                    timelineParkingSpace.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_parking
                        )
                    )
                    parkingContainer.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background
                    )
                    parkingFactTimeText.text = "Прогноз"

                    parkingFactTimeText.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.info
                        )
                    )

                    tvFactTime.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.info
                        )
                    )
                    tvFactTime.text =
                        SimpleDateFormat("HH:mm").format(Date(entity.planTimeStart))
                }

                if (entity.statusType == StatusType.SUCCESS) {

                    timelineParkingSpace.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_parking_space
                        )
                    )
                    parkingFactTimeText.text = "Факт"

                    tvFactTime.text =
                        SimpleDateFormat("HH:mm").format(Date(entity.changeTime))

                    tvFactTime.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.green_text
                        )
                    )

                    parkingFactTimeText.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.green_text
                        )
                    )

                    parkingContainer.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background
                    )

                }
                parkingSpaceLay.isVisible = true
                unloadingLayNew.isVisible = false
                unloadingLay.isVisible = false
            }

            //Разгрузка
            VisitPointType.Type.Recycling, VisitPointType.Type.Portal -> {
                unloadingText.text = entity.visitPoint.name

                if (entity.isCurrent) {
                    timelineUnloading.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_purple_car
                        )
                    )
                    layUnloadin.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background_accent
                    )

                } else {
                    timelineUnloading.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_auto_default_or
                        )
                    )

                    layUnloadin.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background
                    )

                }

                if (entity.statusType == StatusType.SUCCESS) {
                    unloadingFactTime.text = "Факт"
                    unloadingFactTime.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.green_text
                        )
                    )
                    actualTimeText.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.green_text
                        )
                    )
                    actualTimeText.text =
                        SimpleDateFormat("HH:mm").format(Date(entity.changeTime))

                    timelineUnloading.setImageDrawable(
                        getDrawable(
                            itemView.context,
                            R.drawable.ic_unload_done
                        )
                    )

                    layUnloadin.background = getDrawable(
                        itemView.context,
                        R.drawable.shadow_background
                    )
                }

                unloadingLayNew.isVisible = true
                parkingSpaceLay.isVisible = false
                unloadingLay.isVisible = false
            }
        }
    }

    private fun getIcon(condition: Boolean, trueIcon: Drawable?, falseIcon: Drawable?) =
        if (condition) trueIcon else falseIcon

    companion object {

        fun create(
            parent: ViewGroup,
            viewType: Int,
            clickLambda: (TaskRelations) -> Unit,
            routeInteractor: RouteInfo,
            getListCouponsModel: List<GetListCouponsModel>
        ) =
            RouteStandViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_task_new, parent, false),
                viewType,
                clickLambda,
                routeInteractor,
                getListCouponsModel
            )
    }

    private fun showInternetConnection(value: Boolean) {
//        if (value) {
//            windowVisibility.setImageResource(R.drawable.ic_see)
//        } else {
//            windowVisibility.setImageResource(R.drawable.ic_no_see)
//        }
    }

    private fun adapterInfo(entity: TaskRelations): MutableList<TaskItemPreviewData> {
        val supportedGarbageTypes = routeInfo.unit.vehicle.supportedGarbageTypes.map { it.id }
        val ctList =
            entity.task.taskItems.mapNotNull { ti -> entity.task.stand?.containerGroups?.findLast { cG -> cG.containerType.id == ti.containerTypeId } }
        return ctList.map {
            val supportedGarbageType =
                it.garbageType.id in supportedGarbageTypes
            TaskItemPreviewData(
                it.containerType,
                it.garbageType,
                entity.task.containerAction,
                if (supportedGarbageType) entity.task.taskItems.filter { ti -> ti.containerTypeId == it.containerType.id }
                    .map { item -> item.planCount }.sum() else it.count,
                supportedGarbageType,
                entity
            )
        }.toMutableList()
    }

}

