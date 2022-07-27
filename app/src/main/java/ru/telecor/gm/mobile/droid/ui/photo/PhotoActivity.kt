package ru.telecor.gm.mobile.droid.ui.photo

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_photo.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.presentation.photo.PhotoPresenter
import ru.telecor.gm.mobile.droid.presentation.photo.PhotoView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.photo.rv.PhotoAdapter
import ru.telecor.gm.mobile.droid.ui.photomake.PhotoMakeActivity
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.telecor.gm.mobile.droid.utils.conect
import toothpick.Toothpick

class PhotoActivity : BaseActivity(), PhotoView {

    override val layoutResId = R.layout.activity_photo

    val TAG = javaClass.simpleName

    @InjectPresenter
    lateinit var presenter: PhotoPresenter

    @ProvidePresenter
    fun providePresenter(): PhotoPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(PhotoPresenter::class.java).apply {
                this.photoType =
                    intent.extras?.getSerializable(PHOTO_TYPE_ENUM_KEY) as PhotoType
                this.routeId = intent.extras?.getString(ROUTE_CODE_KEY)?.toLong() ?: -1
                this.taskId = intent.extras?.getString(TASK_ID_KEY)?.toLong() ?: -1
            }
    }

    companion object {

        fun createIntent(
            activity: Activity,
            type: PhotoType,
            routeId: String,
            taskId: String,
        ) = Intent(activity, PhotoActivity::class.java).apply {
            putExtra(PHOTO_TYPE_KEY, type)
            putExtra(ROUTE_CODE_KEY, routeId)
            putExtra(TASK_ID_KEY, taskId)
            putExtra(PHOTO_TYPE_ENUM_KEY, type)
        }

        private const val PHOTO_TYPE_KEY = "photoType"
        private const val ROUTE_CODE_KEY = "routeCode"
        private const val TASK_ID_KEY = "taskId"
        private const val PHOTO_TYPE_ENUM_KEY = "photoTypeEnumerated"
    }


    var rvAdapter = PhotoAdapter { photo ->
        presenter.onPhotoDeleteClicked(photo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnConfirmPhoto.setOnClickListener {
            presenter.onConfirmButtonClicked()
        }

        topAppBar.setNavigationOnClickListener { presenter.onCancelButtonClicked() }

//        fabAddPhoto.setOnClickListener(10000) {
//            if (conect(this, this)){
//                presenter.onAddButtonClicked(
//                    LocationUtils.getBestLocation(
//                        this
//                    )
//                )
//            }
//        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            rvPhotos.layoutManager =
                StaggeredGridLayoutManager(3, Configuration.ORIENTATION_PORTRAIT)
        else
            rvPhotos.layoutManager =
                StaggeredGridLayoutManager(2, Configuration.ORIENTATION_PORTRAIT)

        rvPhotos.adapter = rvAdapter
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (conect(this, this)){
            presenter.onAddButtonClicked(
                LocationUtils.getBestLocation(
                    this
                )
            )
        }
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
    private fun getCamera(path: String) {
        withPermission(android.Manifest.permission.CAMERA, {

            startActivityForResult(
                PhotoMakeActivity.createIntent(this, path),
                REQUEST_TAKE_PHOTO
            )
        },
            { showMessage(getString(R.string.error_permission_denied)) })
    }

    override fun photoWithGPSWanted() {
        presenter.onAddButtonClicked(LocationUtils.getBestLocation(this))
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

    override fun showListOfPhotos(list: List<ProcessingPhoto>) {
        rvAdapter.setList(list)
        tvEmptyListWarning.visible(list.isEmpty())
    }

    override fun setAppBarTitle(title: String) {
        topAppBar.title = title
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0
    }
}
