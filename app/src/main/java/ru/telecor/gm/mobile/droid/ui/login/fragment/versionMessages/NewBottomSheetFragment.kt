package ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages

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
import kotlinx.android.synthetic.main.fragment_version_bottom_sheet.*
import ru.telecor.gm.mobile.droid.utils.LoadingAlert
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.updateDialog.UpdateDialogPresenter
import ru.telecor.gm.mobile.droid.presentation.updateDialog.UpdateDialogView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.updatingVersion.UpdatingVersionSheetFragment
import timber.log.Timber
import toothpick.Toothpick
import java.io.File
import kotlin.system.exitProcess

class NewBottomSheetFragment(
    private var updatingVersionSheetFragment: UpdatingVersionSheetFragment? = null
) : BaseBottomSheetFragment(), UpdateDialogView {

    override val layoutRes = R.layout.fragment_version_bottom_sheet

    @InjectPresenter
    lateinit var presenter: UpdateDialogPresenter

    companion object {

        private const val permResult = 1235
        private var VERSION = "version"
        private var nameVersion = ""
        private var installBoolean: Boolean = false

        fun newInstance(
            version: String,
            updating: UpdatingVersionSheetFragment? = null,
            name: String,
            install: Boolean? = false
        ) = NewBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString(VERSION, version)
                updatingVersionSheetFragment = updating
                nameVersion = name
                installBoolean = install!!
            }
        }
    }

    @ProvidePresenter
    fun providePresenter(): UpdateDialogPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(UpdateDialogPresenter::class.java)
            .apply {
                arguments?.getString(VERSION)?.let {
                    version = it
                }
                arguments?.let {
                    install = installBoolean
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false

        if (nameVersion != ""){
            btnPositive.text = nameVersion
        }

        if (installBoolean){
            tvMessage.text = resources.getString(R.string.an_update_is_needed)
        }

        tvVersion.text = BuildConfig.VERSION_NAME

        alertDialog = LoadingAlert(requireActivity())
        initClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == permResult && resultCode == Activity.RESULT_OK) {
            presenter.onUpdateAccepted()
        }
    }

    private fun initClick() {
        btnPositive.setOnClickListener {
            presenter.prefs.isLastInstallation = true
            presenter.prefs.isInstallationComplete = true
            alertDialog.show()
            checkForPermissions({
                presenter.onUpdateAccepted()
            }, {})
        }

        btnNegative.setOnClickListener {
            if (presenter.install) {
                exitProcess(0)
            } else {
                val bottomSheetDialogFragment = UpdatingVersionSheetFragment(null, this)
                bottomSheetDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    bottomSheetDialogFragment.tag
                )
                presenter.deleteVersion()
            }
        }
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    override fun onResume() {
        super.onResume()
        updatingVersionSheetFragment?.dismiss()
    }

    override fun setLoadingState(value: Boolean) {

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
        alertDialog.hide()
    }

    override fun installationAPK() {
        dismiss()
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
}
