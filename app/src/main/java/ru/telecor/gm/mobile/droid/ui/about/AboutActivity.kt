package ru.telecor.gm.mobile.droid.ui.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_about.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.presentation.about.AboutPresenter
import ru.telecor.gm.mobile.droid.presentation.about.AboutView
import ru.telecor.gm.mobile.droid.ui.base.BaseActivity
import ru.telecor.gm.mobile.droid.ui.updateDialog.UpdateDialogFragment
import timber.log.Timber
import toothpick.Toothpick
import java.io.File

class AboutActivity : BaseActivity(), AboutView {

    @InjectPresenter
    lateinit var presenter: AboutPresenter

    @ProvidePresenter
    fun providePresenter(): AboutPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(AboutPresenter::class.java)
    }

    override val layoutResId = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mToolbar.setNavigationOnClickListener {
            presenter.onBackButtonClicked()
        }
        btnUpdateApp.setOnClickListener { presenter.onUpdateButtonClicked(BuildVersion.ALPHA, false) }
        btnUpdateAppBeta.setOnClickListener { presenter.onUpdateButtonClicked(BuildVersion.BETA, false) }
    }

    override fun setLoadingState(isUpdating: Boolean, version: BuildVersion) {
        if (isAlpha(version)){
            pbUpdate.visible(isUpdating)
        }else{
            pbUpdateBeta.visible(isUpdating)
        }
    }

    private fun isAlpha(version: BuildVersion): Boolean {
        return version == BuildVersion.ALPHA
    }

    override fun setCurrentVersion(version: String) {

        tvVersionTitle.text = BuildVersion.ALPHA.viewName
        tvVersionTitleBeta.text =  BuildVersion.BETA.viewName

        if (presenter.cVersion == BuildVersion.ALPHA) {
            tvVersionBlock.visible(true)
            tvVersion.text = version
            btnUpdateAppBeta.text = resources.getString(R.string.about_activity_switch_dialog_positive)
        }else{
            tvVersionBlockBeta.visible(true)
            tvVersionBeta.text = version
            btnUpdateApp.text = resources.getString(R.string.about_activity_switch_dialog_positive)
        }
    }

    override fun setNewestVersion(newestVersion: String,version: BuildVersion) {
        if(isAlpha(version)){
            tvLastVersion.text = newestVersion

        }else{
            tvLastVersionBeta.text = newestVersion
        }

    }

    override fun showUpdateDialog(version: BuildVersion) {
        UpdateDialogFragment.newInstance(version.serverName).show(supportFragmentManager, "updateDialog")
    }

    override fun finishScreen() {
        onBackPressed()
    }

    override fun installAPK(path: String) {
        val file = File(path)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(applicationContext, File(path)),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Timber.e("Error in opening the file!")
            }
        } else {
            Toast.makeText(applicationContext, "installing", Toast.LENGTH_LONG).show()
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
