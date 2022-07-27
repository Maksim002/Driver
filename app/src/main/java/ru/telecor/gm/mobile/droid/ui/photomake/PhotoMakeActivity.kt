package ru.telecor.gm.mobile.droid.ui.photomake

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Size
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_photo_make.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.presentation.photoMake.PhotoMakePresenter
import ru.telecor.gm.mobile.droid.presentation.photoMake.PhotoMakeView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import timber.log.Timber
import toothpick.Toothpick
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.lang.Exception

class PhotoMakeActivity : BaseActivity(), PhotoMakeView {

    private var imageCapture: ImageCapture? = null
    private lateinit var tempFile: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoPath: String

    override val layoutResId = R.layout.activity_photo_make

    @InjectPresenter
    lateinit var presenter: PhotoMakePresenter

    @ProvidePresenter
    fun providePresenter(): PhotoMakePresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(PhotoMakePresenter::class.java)
    }

    companion object {

        fun createIntent(activity: Activity, photoPath: String) =
            Intent(activity, PhotoMakeActivity::class.java).apply {
                putExtra(PHOTO_PATH, photoPath)
            }

        private const val PHOTO_PATH = "photoPath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        withPermission(Manifest.permission.CAMERA, {
            try {
                startCamera()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }, { showMessage(getString(R.string.error_permission_denied)) })

        fabTakePhoto.setOnClickListener { presenter.onTakePhotoButtonPressed() }

        fabFlashlight.setOnClickListener { presenter.onFlashButtonPressed() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraProviderFeature.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFeature.get()

            val size =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Size(1080, 1920)
                } else {
                    Size(1920, 1080)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder()
                .setTargetResolution(size)
                .build()
                .also {
                    it.setSurfaceProvider(pvCameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(size)
                .build()

            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
            )

            val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    // Get the camera's current zoom ratio
                    val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F

                    // Get the pinch gesture's scaling factor
                    val delta = detector.scaleFactor

                    // Update the camera's zoom ratio. This is an asynchronous operation that returns
                    // a ListenableFuture, allowing you to listen to when the operation completes.
                    camera.cameraControl.setZoomRatio(currentZoomRatio * delta)

                    // Return true, as the event was handled
                    return true
                }
            }
            val scaleGestureDetector = ScaleGestureDetector(this, listener)

            // Attach the pinch gesture listener to the viewfinder
            pvCameraView.setOnTouchListener { _, event ->
                scaleGestureDetector.onTouchEvent(event)
                return@setOnTouchListener true
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun setCurrentFlashState(isFlashEnabled: Boolean) {
        if (isFlashEnabled) {
            fabFlashlight.setImageDrawable(getDrawable(R.drawable.ic_baseline_flash_on_24))
        } else {
            fabFlashlight.setImageDrawable(getDrawable(R.drawable.ic_baseline_flash_off_24))
        }
    }

    override fun takePhoto(isFlashEnabled: Boolean) {
        val imageCapture = imageCapture ?: return
        val photoFile = tempFile
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.flashMode = if (isFlashEnabled) {
            ImageCapture.FLASH_MODE_ON
        } else {
            ImageCapture.FLASH_MODE_OFF
        }
        imageCapture
            .takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        presenter.onPhotoTaken()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Timber.e(exception)
                        showMessage(getString(R.string.error_base))
                    }
                })
    }

    override fun showPreviewDialog(value: Boolean) {
        if (!value) return
        val iv = ImageView(this)
        val drawable = Drawable.createFromPath(tempFile.path)
        iv.setImageDrawable(drawable)
        MaterialAlertDialogBuilder(this)
            .setView(iv)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.photo_make_activity_dialog_button_accept)) { _, _ -> presenter.onPhotoAccepted() }
            .setNegativeButton(getString(R.string.photo_make_activity_dialog_button_decline)) { di, _ ->
                di.dismiss()
                presenter.onPhotoRejected()
            }
            .show()
    }

    override fun setResultAndFinish() {
        try {
            photoPath = intent.getStringExtra(PHOTO_PATH) ?: return
            val photoFile = File(photoPath)
            if (!tempFile.exists()) {
                tempFile.mkdir()
            }
            if (!photoFile.exists()) {
                photoFile.mkdir()
            }
            tempFile.copyTo(photoFile, true)
            setResult(RESULT_OK)
            finish()
        }catch (e: Exception){
            Timber.e(e)
            showMessage(getString(R.string.error_base))
        }
    }

    override fun back() {
        finish()
    }

    override fun setTempFile(file: File) {
        tempFile = file
    }
}
