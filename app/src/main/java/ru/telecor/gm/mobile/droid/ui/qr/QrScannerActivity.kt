package ru.telecor.gm.mobile.droid.ui.qr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_photo_make.pvCameraView
import kotlinx.android.synthetic.main.activity_qr_scanner.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.CarryContainerHistoryItem
import ru.telecor.gm.mobile.droid.entities.ContainerHistoryActionType
import ru.telecor.gm.mobile.droid.presentation.qr.QrScannerPresenter
import ru.telecor.gm.mobile.droid.presentation.qr.QrScannerView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.containershistory.ContainersHistoryActivity
import timber.log.Timber
import toothpick.Toothpick
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias BarcodeListener = (barcode: String) -> Unit

class QrScannerActivity : BaseActivity(), QrScannerView {

    override val layoutResId = R.layout.activity_qr_scanner
    private lateinit var cameraExecutor: ExecutorService
    private var isScannerActive = true

    @InjectPresenter
    lateinit var presenter: QrScannerPresenter

    @ProvidePresenter
    fun providePresenter(): QrScannerPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(QrScannerPresenter::class.java)
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

        cameraExecutor = Executors.newSingleThreadExecutor()

        btnBack.setOnClickListener { onBackPressed() }
        fabHistory.setOnClickListener {
            startActivity(Intent(this, ContainersHistoryActivity::class.java))
        }
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

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(size)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { qrResult ->
                        pvCameraView.post {
                            Timber.d("QRCodeAnalyzer. Barcode scanned: $qrResult")
                            if (isScannerActive) {
                                presenter.onCodeScanned(qrResult)
                            }
                        }
                    })
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalysis,
            )

        }, ContextCompat.getMainExecutor(this))
    }

    override fun setDialogVisible(
        value: Boolean,
        carryContainerHistoryItem: CarryContainerHistoryItem?
    ) {
        isScannerActive = !value
        if (value) {
            carryContainerHistoryItem?.let {
                MaterialAlertDialogBuilder(this)
                    .setMessage(
                        when (it.containerActionType) {
                            ContainerHistoryActionType.ADD -> "${it.carryContainer.id}\nДобавить этот контейнер?"
                            ContainerHistoryActionType.REMOVE -> "${it.carryContainer.id}\nУбрать этот контейнер?"
                        }
                    )
                    .setPositiveButton("Да") { _, _ ->
                        presenter.onActionAccepted(carryContainerHistoryItem)
                    }
                    .setNegativeButton("Отмена") { _, _ ->
                        presenter.onActionCancelled()
                    }
                    .setOnDismissListener {
                        presenter.onActionCancelled()
                    }
                    .show()
            }
        }
    }

    override fun showActionCompletedMessage(actionType: ContainerHistoryActionType) {
        val text = when (actionType) {
            ContainerHistoryActionType.ADD -> getString(R.string.qr_scanner_screen_snackbar_add_text)
            ContainerHistoryActionType.REMOVE -> getString(R.string.qr_scanner_screen_snackbar_remove_text)
        }
        Snackbar.make(
            window.decorView.findViewById(android.R.id.content),
            text,
            Snackbar.LENGTH_SHORT
        ).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            show()
        }
    }

    override fun showErrorDialog(message: String) {
        isScannerActive = false
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("Хорошо") { di, _ -> di.dismiss() }
            .setOnDismissListener { isScannerActive = true }
            .show()
    }
}


class BarcodeAnalyzer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {
    // Get an instance of BarcodeScanner
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    for (barcode in barcodes) {
                        barcodeListener(barcode.rawValue ?: "")
                    }
                }
                .addOnFailureListener {
                    // You should really do something about Exceptions
                }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }
}