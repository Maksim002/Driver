package ru.telecor.gm.mobile.droid.ui.updateDialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_fragment_update.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.updateDialog.UpdateDialogPresenter
import ru.telecor.gm.mobile.droid.presentation.updateDialog.UpdateDialogView
import ru.telecor.gm.mobile.droid.ui.base.BaseDialogFragment
import timber.log.Timber
import toothpick.Toothpick
import java.io.File
import kotlin.system.exitProcess

class UpdateDialogFragment : BaseDialogFragment(), UpdateDialogView {

    @InjectPresenter
    lateinit var presenter: UpdateDialogPresenter

    companion object {

        private const val permResult = 1235
        private const val VERSION = "version"

        fun newInstance(version: String) = UpdateDialogFragment().apply {
            arguments = Bundle().apply {
                putString(VERSION, version)
            }
        }
    }

    @ProvidePresenter
    fun providePresenter(): UpdateDialogPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(UpdateDialogPresenter::class.java)
            .apply { arguments?.getString(VERSION)?.let {
                version = it
            } }
    }

    private lateinit var loadingDialog: androidx.appcompat.app.AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_update, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = MaterialAlertDialogBuilder(context!!)
            .setView(ProgressBar.generateViewId())
            .setCancelable(false)
            .create()

        isCancelable = false

        btnPositive.setOnClickListener {
            checkForPermissions({
                presenter.onUpdateAccepted()
            }, {})
        }
        btnNegative.setOnClickListener {
            if (presenter.install){
                exitProcess(0)
            }else{
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == permResult && resultCode == Activity.RESULT_OK) {
            presenter.onUpdateAccepted()
        }
    }

    private fun checkForPermissions(success: () -> Unit, error: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity!!.packageManager.canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(
                            String.format(
                                "package:%s",
                                activity!!.packageName
                            )
                        )
                    ), 1234
                )
            } else {
                error()
            }
        }
        withPermission(Manifest.permission.READ_EXTERNAL_STORAGE, {
            withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, {
                success()
            }, { error() })
        }, { error() })
    }

    override fun installAPK(path: String) {
        val file = File(path)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(activity!!.applicationContext, File(path)),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                activity!!.applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Timber.e("Error in opening the file!")
            }
        } else {
            Toast.makeText(activity!!.applicationContext, "installing", Toast.LENGTH_LONG).show()
        }
    }

    override fun installationAPK() {}

    private fun uriFromFile(context: Context, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }

    override fun setLoadingState(value: Boolean) {
        btnNegative.visible(!value, View.INVISIBLE)
        pbLoading.visible(value)
    }

    override fun switchingVersions(checkedVersion: BuildVersion) {
        tvMessage.text =
            resources.getString(R.string.about_activity_switch_dialog_title)
                .replace("%s", checkedVersion.viewName)
        btnPositive.text = resources.getString(R.string.about_activity_switch_dialog_positive)
        btnPositive.textSize = 10F

        tvTitle.visible(false)
        btnNegative.visible(true, View.INVISIBLE)
    }
}
