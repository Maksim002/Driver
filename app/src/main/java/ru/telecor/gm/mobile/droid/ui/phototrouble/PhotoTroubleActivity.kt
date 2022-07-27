package ru.telecor.gm.mobile.droid.ui.phototrouble

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_photo_trouble.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.phototrouble.PhotoTroublePresenter
import ru.telecor.gm.mobile.droid.presentation.phototrouble.PhotoTroubleView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.photomake.PhotoMakeActivity
import ru.telecor.gm.mobile.droid.ui.phototrouble.rv.PhotoTroubleAdapter
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.telecor.gm.mobile.droid.utils.conect
import toothpick.Toothpick

class PhotoTroubleActivity : BaseActivity(), PhotoTroubleView {

    override val layoutResId: Int = R.layout.activity_photo_trouble

    @InjectPresenter
    lateinit var presenter: PhotoTroublePresenter

    @ProvidePresenter
    fun providePresenter(): PhotoTroublePresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(PhotoTroublePresenter::class.java).apply {
                this.routeId =
                    intent.extras?.getString(ROUTE_CODE_KEY)?.toLong() ?: -1
                this.taskId = intent.extras?.getString(TASK_ID_KEY)?.toLong() ?: -1
            }
    }

    companion object {

        fun createIntent(
            activity: Activity,
            routeId: String,
            taskId: String
        ) = Intent(activity, PhotoTroubleActivity::class.java).apply {
            putExtra(ROUTE_CODE_KEY, routeId)
            putExtra(TASK_ID_KEY, taskId)
        }

        private const val PHOTO_TYPE_KEY = "photoType"
        private const val ROUTE_CODE_KEY = "routeCode"
        private const val TASK_ID_KEY = "taskId"
        private const val PHOTO_TYPE_ENUM_KEY = "photoTypeEnumerated"
    }

    var rvAdapter = PhotoTroubleAdapter { photo -> presenter.onPhotoDeleteClicked(photo) }
    var rvBlockageAdapter = PhotoTroubleAdapter { photo -> presenter.onPhotoDeleteClicked(photo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topAppBar.setNavigationOnClickListener { presenter.onCancelButtonClicked() }

        btnConfirmPhoto.setOnClickListener {
            presenter.onConfirmButtonClicked()
        }

        btnAddPhotosProblem.setOnClickListener(10000) {
            presenter.onAddProblemButtonClicked(LocationUtils.getBestLocation(this))
        }
        btnAddPhotosBlockage.setOnClickListener(10000) {
            presenter.onAddBlockageButtonClicked(LocationUtils.getBestLocation(this))
        }

        rvPhotosProblem.adapter = rvAdapter
        rvPhotosBlockage.adapter = rvBlockageAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        this.recreate()
    }

    val REQUEST_TAKE_PHOTO = 1


    override fun startInternalCameraForResult(path: String) {
        presenter.getStatusPhoto(false)
        getCamera(path)
    }

    override fun startExternalCameraForResult(path: String) {
        presenter.getStatusPhoto(true)
        getCamera(path)
    }

    override fun back() {
        onBackPressed()
    }

    //Opening the camera
    private fun getCamera(path: String){
        withPermission(android.Manifest.permission.CAMERA, {
            if (conect(this, this)){
                startActivityForResult(
                    PhotoMakeActivity.createIntent(this, path),
                    REQUEST_TAKE_PHOTO
                )
            }
        },
            { showMessage(getString(R.string.error_permission_denied)) })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            presenter.onPhotoDone()
        }
    }

    override fun setResultAndFinish(path: String) {
        setResult(RESULT_OK, Intent().apply { putExtra("path", path) })
        finish()
    }

    override fun cancel() {
        onBackPressed()
    }

    override fun showListOfBlockagePhotos(list: List<ProcessingPhoto>) {
        rvBlockageAdapter.setList(list)
        tvEmptyWarningBlockage.visible(list.isEmpty())
    }

    override fun showListOfPhotos(list: List<ProcessingPhoto>) {
        rvAdapter.setList(list)
        tvEmptyWarning.visible(list.isEmpty())
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0
    }
}
