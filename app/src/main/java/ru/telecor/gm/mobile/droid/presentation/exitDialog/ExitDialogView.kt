package ru.telecor.gm.mobile.droid.presentation.exitDialog

import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface ExitDialogView : BaseView {

    fun showNumUploadPhoto(
        float: Float,
        loadCount: Int,
        maxCount: Int,
        destroyCount: Int,
        loadStatus: LoadStatus
    )

    fun showNumUploadRoute(float: Float, loadCount: Int, maxCount: Int)

    fun showSuccessDialog()
    fun showFailDialog()
    fun showLoadingRouteDialog()

    fun showDataPreparationDialog()

    fun showEstimatedTimeOfUnloading(value: Int, loadStatus: LoadStatus)

    fun setLoadingState(value: WorkStatus)

    enum class LoadStatus {
        INDEFINITE,
        START,
        FINiSH,
        ERROR
    }

    enum class WorkStatus {
        PREPARE,
        UPLOAD,
        FINISH,
        ERROR

    }

}