package ru.telecor.gm.mobile.droid.ui.login.fragment.activity

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import ru.telecor.gm.mobile.droid.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_updating.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.updateActivity.UpdatingPresenter
import ru.telecor.gm.mobile.droid.presentation.updateActivity.UpdatingView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import timber.log.Timber
import toothpick.Toothpick
import java.io.File
import kotlin.system.exitProcess

class UpdatingActivity : BaseActivity(), UpdatingView {

    private var permResult = 1235

    override val layoutResId = R.layout.activity_updating

    @InjectPresenter
    lateinit var presenter: UpdatingPresenter

    @ProvidePresenter
    fun providePresenter(): UpdatingPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(UpdatingPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertDialog = LoadingAlert(this)
        initClick()
    }

    private fun initClick() {
        btnPositive.setOnClickListener {
            alertDialog.show()
            checkForPermissions({
                presenter.onUpdateAccepted()
            }, {})
        }

        btnNegative.setOnClickListener {
            if (presenter.install) {
                exitProcess(0)
            } else {
                presenter.deleteVersion()
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UpdatingActivity().permResult && resultCode == Activity.RESULT_OK) {
            presenter.onUpdateAccepted()
        }
    }

    override fun setLoadingState(isUpdating: Boolean, version: BuildVersion?) {

    }

    override fun switchingVersions(checkedVersion: BuildVersion) {
        tvMessage.text =
            resources.getString(R.string.about_activity_switch_dialog_title)
                .replace("%s", checkedVersion.viewName)

        btnNegative.visible(true, View.INVISIBLE)
    }

    override fun installAPK(path: String) {
            val file = File(path)
            if (file.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    uriFromFile(this.applicationContext, File(path)),
                    "application/vnd.android.package-archive"
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    this.applicationContext.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Timber.e("Error in opening the file!")
                }
            } else {
                Toast.makeText(this.applicationContext, "installing", Toast.LENGTH_LONG).show()
            }
            alertDialog.hide()
    }

    override fun installationAPK() {

    }

    override fun setNewestVersion(newestVersion: String, version: BuildVersion) {

    }

    override fun showUpdateDialog(version: BuildVersion) {
       presenter.versions = version.serverName
    }

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

    private fun checkForPermissions(success: () -> Unit, error: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!this.packageManager.canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(
                            String.format(
                                "package:%s",
                                this.packageName
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
}