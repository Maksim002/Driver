package ru.telecor.gm.mobile.droid.model.entities

import ru.telecor.gm.mobile.droid.presentation.exitDialog.ExitDialogView

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.model.entities
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 28.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class ExportedDataInfo(
    var photoCount: Int,
    var exportedPhotoCount: Int,
    var taskCount: Int,
    var exportedTaskCount: Int,
    var isError: Boolean = false,
    var isFinished: Boolean = false,
    var errorMessage: String? = null,
    var estimatedTime: Int = 0,
    var destroyPhotoCount: Int = 0,
    var workStatus: ExitDialogView.WorkStatus = ExitDialogView.WorkStatus.UPLOAD,
    var loadStatus: ExitDialogView.LoadStatus = ExitDialogView.LoadStatus.START
)