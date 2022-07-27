package ru.telecor.gm.mobile.droid.ui.tasktrouble.fragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_problem_bottom_sheet.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.presentation.tasktrouble.problem.ProblemPresenter
import ru.telecor.gm.mobile.droid.presentation.tasktrouble.problem.ProblemView
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.photomake.PhotoMakeGeneralActivity
import ru.telecor.gm.mobile.droid.ui.tasktrouble.problem.rv.ProblemPhotoAdapter
import ru.telecor.gm.mobile.droid.utils.conect
import ru.telecor.gm.mobile.droid.utils.сomponent.spinner.SpinnerModel
import toothpick.Toothpick

class ProblemBottomSheetFragment(
    var name: String,
    var list: List<TaskFailureReason>, var listener: TroubleListener
) : BaseBottomSheetFragment(), ProblemView {

    private var photoModel: ArrayList<ProcessingPhoto> = arrayListOf()
    private lateinit var params: LinearLayout.LayoutParams

    override val layoutRes = R.layout.fragment_problem_bottom_sheet

    @InjectPresenter
    lateinit var presenter: ProblemPresenter

    @ProvidePresenter
    fun providePresenter(): ProblemPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ProblemPresenter::class.java)
    }

    private val REQUEST_TAKE_PHOTO = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        params = layoutSte.layoutParams as LinearLayout.LayoutParams
        initClick()
//        disableButtons(disablePhotoBtn = false, false)
        orinDevice()
    }

    private fun orinDevice() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        } else {
            params.height = 1100
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height = 1100
        }
    }

    private fun disableButtons(disablePhotoBtn: Boolean) {

        if (!disablePhotoBtn) {
            btnTakeImage.isEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnTakeImage.backgroundTintList =
                    resources.getColorStateList(R.color.gray, resources.newTheme())
            }
        } else {
            btnTakeImage.isEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btnTakeImage.backgroundTintList =
                    resources.getColorStateList(R.color.color_text, resources.newTheme())
            }
        }
    }

    private fun initClick() {

        problemDropDownSpinner.setOnClickListener {
            val newList = ArrayList<SpinnerModel>()
            list.forEach {
                newList.add(SpinnerModel(it.id, it.name, it.name))
            }
            problemDropDownSpinner.setOnClick(newList)
            problemDropDownSpinner.clickItemListener.observe(viewLifecycleOwner, {
                val index =
                    newList.indexOfFirst { list -> list.id == it.id && list.text == it.text }
                if (index != -1) {
                    listener.setOnClickSuccessfully(index)
                }
                checkingOccupancy(false)
            })
        }
        if (name != "") {
            problemDropDownSpinner.defaultValue(name)

            checkingOccupancy(false)
        }else{
            checkingOccupancy(true)
        }

        btnItemCancel.setOnClickListener {
            listener.setOnClickFailure()
            dismiss()
        }

        btnItemOk.setOnClickListener {
            listener.setOnClickComplete(this)
//            dismiss()
        }

        btnTakeImage.setOnClickListener {
            presenter.photoButtonClicked()
        }
    }

    override fun getTheme(): Int {
        return if (Build.VERSION.SDK_INT > 22) {
            R.style.AppBottomSheetDialogTheme
        } else {
            0
        }
    }

    private fun checkingOccupancy(boolean: Boolean) {
        if (boolean) {
            btnItemOk.isEnabled = false
            btnItemOk.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray
                )
            )
        } else {
            btnItemOk.isEnabled = true
            btnItemOk.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.white_green_button_style)
        }
    }

    override fun startExternalCameraForResult(path: String) {
        presenter.getStatusPhoto(true)
        getCamera(path)
    }

    //Opening the camera
    private fun getCamera(path: String) {
        withPermission(android.Manifest.permission.CAMERA, {
            if (conect(requireContext(), requireActivity())) {
                startActivityForResult(
                    PhotoMakeGeneralActivity.createIntent(
                        requireActivity(),
                        path,
                        presenter.photoType
                    ),
                    REQUEST_TAKE_PHOTO
                )
            }
        },
            { showMessage(getString(R.string.error_permission_denied)) })
    }

    override fun showPhoto(model: ArrayList<GarbagePhotoModel>) {
        try {
            if (model.firstOrNull()?.item != null) {
                photoModel = model.firstOrNull()?.item!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val adapters = ProblemPhotoAdapter(deleteOption = { photo ->
            presenter.onPhotoDeleteClicked(photo)
        }, photo = {
            dismiss()
            presenter.getPhoto(it)
        })
        adapters.update(model.firstOrNull()?.item!!)
        itemPhotoRec.adapter = adapters
        itemPhotoRec.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        if (model.firstOrNull()?.item!!.isNotEmpty()) {
//            disableButtons(true, disableOkBtn = true)
        }
    }

    override fun setMandatoryPhoto(value: Boolean) {
        if (!value) {
            disableButtons(true)
        } else {
            disableButtons(true)
        }
    }

    override fun defaultValue(name: String) {}

    override fun showMessage(msg: String) {}

    interface TroubleListener {
        fun setOnClickSuccessfully(position: Int)
        fun setOnClickComplete(l: ProblemBottomSheetFragment)
        fun setOnClickFailure()
    }
}