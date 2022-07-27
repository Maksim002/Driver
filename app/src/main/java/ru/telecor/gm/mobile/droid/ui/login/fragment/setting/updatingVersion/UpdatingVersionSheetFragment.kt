package ru.telecor.gm.mobile.droid.ui.login.fragment.setting.updatingVersion

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import ru.telecor.gm.mobile.droid.utils.LoadingAlert
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.*
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.btnUpdateApp
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.btnUpdateAppBeta
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.pbUpdate
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.pbUpdateBeta
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvLastVersion
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvLastVersionBeta
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersion
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersionBeta
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersionBlock
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersionBlockBeta
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersionTitle
import kotlinx.android.synthetic.main.fragment_updating_version_sheet.tvVersionTitleBeta
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.updateVersion.UpdatingVersionSheetPresenter
import ru.telecor.gm.mobile.droid.presentation.updateVersion.UpdatingVersionSheetView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.versionMessages.NewBottomSheetFragment
import timber.log.Timber
import toothpick.Toothpick
import java.io.File

class UpdatingVersionSheetFragment(
    private val settingBottomSheetFragment: SettingBottomSheetFragment? = null,
    private val newBottomSheetFragment: NewBottomSheetFragment? = null
) : BaseBottomSheetFragment(), UpdatingVersionSheetView {

    private var name = ""

    override val layoutRes = R.layout.fragment_updating_version_sheet

    @InjectPresenter
    lateinit var presenter: UpdatingVersionSheetPresenter

    @ProvidePresenter
    fun providePresenter(): UpdatingVersionSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(UpdatingVersionSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertDialog = LoadingAlert(requireActivity())
        isClick()
    }

    private fun isClick() {
        toChangeServer.setOnClickListener {
            val bottomSheetDialogFragment = SettingBottomSheetFragment(
                null, null, null, this
            )
            bottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag
            )
        }

        btnUpdateApp.setOnClickListener {
            getInstallationRole(btnUpdateApp)
            presenter.onUpdateButtonClicked(
                BuildVersion.ALPHA,
                false
            )
        }
        btnUpdateAppBeta.setOnClickListener {
            getInstallationRole(btnUpdateAppBeta)
            presenter.onUpdateButtonClicked(
                BuildVersion.BETA,
                false
            )
        }

        settingVersionTxt.text = BuildConfig.VERSION_NAME
    }

    //Проверяет первичная и вторичая установка
    private fun getInstallationRole(view: TextView) {
        if (view.text == resources.getString(R.string.about_activity_switch_dialog_positive)) {
            presenter.setInstallationRole(false)
        } else {
            presenter.setInstallationRole(true)
        }
        name = view.text.toString()
    }

    override fun onResume() {
        super.onResume()
        settingBottomSheetFragment?.dismiss()
        newBottomSheetFragment?.dismiss()
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}

    override fun setLoadingState(isUpdating: Boolean, version: BuildVersion) {
        if (isAlpha(version)) {
            pbUpdate.visible(isUpdating)
        } else {
            pbUpdateBeta.visible(isUpdating)
        }
    }

    override fun setCurrentVersion(version: String) {
        tvVersionTitle.text = BuildVersion.ALPHA.viewName
        tvVersionTitleBeta.text = BuildVersion.BETA.viewName

        if (presenter.cVersion == BuildVersion.ALPHA) {
            tvVersionBlock.visible(true)
            tvVersion.text = version
            btnUpdateAppBeta.text =
                resources.getString(R.string.about_activity_switch_dialog_positive)
        } else {
            tvVersionBlockBeta.visible(true)
            tvVersionBeta.text = version
            btnUpdateApp.text = resources.getString(R.string.about_activity_switch_dialog_positive)
        }
    }

    private fun isAlpha(version: BuildVersion): Boolean {
        return version == BuildVersion.ALPHA
    }

    override fun setNewestVersion(newestVersion: String, version: BuildVersion) {
        if (isAlpha(version)) {
            tvLastVersion.text = newestVersion
        } else {
            tvLastVersionBeta.text = newestVersion
        }
    }

    override fun finishScreen() {
        requireActivity().onBackPressed()
    }

    override fun showUpdateDialog(version: BuildVersion) {
//        UpdateDialogFragment.newInstance(version.serverName).show(requireActivity().supportFragmentManager, "updateDialog")
        initStartFragment(version)
    }

    private fun initStartFragment(version: BuildVersion) {
        NewBottomSheetFragment.newInstance(version.serverName, this, name)
            .show(requireActivity().supportFragmentManager, "newBottomSheetFragment")
    }

    override fun installAPK(path: String) {
        val file = File(path)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(requireContext(), File(path)),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                requireContext().startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Timber.e("Error in opening the file!")
            }
        } else {
            Toast.makeText(requireContext(), "installing", Toast.LENGTH_LONG).show()
        }
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
}