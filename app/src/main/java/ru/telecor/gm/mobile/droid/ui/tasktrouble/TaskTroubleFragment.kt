package ru.telecor.gm.mobile.droid.ui.tasktrouble

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_task_trouble.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.TaskFailureReason
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.photo.GarbagePhotoModel
import ru.telecor.gm.mobile.droid.entities.task.StatusTaskExtended
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.presentation.tasktrouble.TaskTroublePresenter
import ru.telecor.gm.mobile.droid.presentation.tasktrouble.TaskTroubleView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.photo.PhotoActivity
import ru.telecor.gm.mobile.droid.ui.tasktrouble.fragment.ProblemBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.tasktrouble.problem.rv.ImpossibleAdapter
import ru.telecor.gm.mobile.droid.ui.tasktrouble.problem.rv.ProblemPhotoAdapter
import ru.telecor.gm.mobile.droid.utils.millisecondsDate
import toothpick.Toothpick
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskTroubleFragment : BaseFragment(), TaskTroubleView {
    companion object {
        fun newInstance() = TaskTroubleFragment()
    }

    override val layoutRes = R.layout.fragment_task_trouble

    @InjectPresenter
    lateinit var presenter: TaskTroublePresenter

    @ProvidePresenter
    fun providePresenter(): TaskTroublePresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(TaskTroublePresenter::class.java)
    }

    private var adapters = ImpossibleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        btnMakePhotoTrouble.setOnClickListener {
            presenter.photoButtonClicked()
        }

        troubleArrowBack.setOnClickListener {
            presenter.onBackClicked()
        }

        layoutTask.setOnClickListener {
            if (troubleContainerTask.isVisible) {
                troubleTaskDetailsIm.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_drop_down_24
                    )
                )
                troubleContainerTask.isVisible = false
            } else {
                troubleTaskDetailsIm.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_arrow_drop_up_24
                    )
                )
                troubleContainerTask.isVisible = true
            }
        }
    }

    override fun takePhoto(routeCode: String, taskId: String) {
        startActivityForResult(
            activity?.let { it1 ->
                PhotoActivity.createIntent(
                    it1,
                    PhotoType.TASK_TROUBLE,
                    routeCode,
                    taskId
                )
            }, PhotoType.TASK_TROUBLE.ordinal
        )
    }

    override fun setPhotoPreview(path: String) {
        Glide.with(this)
            .load(path)
            .centerCrop()
            .into(ivPhotoTrouble)
        ivPhotoTrouble.visibility = View.VISIBLE
    }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value, View.INVISIBLE)
    }

    override fun setTaskAddress(string: String) {
        troubleTvAddress.text = string
    }

    override fun setGeneralCan(localTaskCache: TaskExtended) {
        if (localTaskCache.priority != null){
            messageCon.visible(localTaskCache.priority.name.isNotEmpty())
            tvTroublePriority.text = localTaskCache.priority.name ?: ""
        }

        tvTroublePreferredTimeText.text = SimpleDateFormat(
            "E HH:mm",
            Locale("ru")
        ).format(Date(localTaskCache.preferredTimeStart))
            .capitalize() + " - " + SimpleDateFormat(
            "E HH:mm", Locale("ru")
        ).format(Date(localTaskCache.preferredTimeEnd)).capitalize()

        tvTroubleFactTimeText.text = millisecondsDate(localTaskCache.planTimeStart)

        val text = localTaskCache.taskItems.map { item -> item.customer.name }
        val oneText = text.toString().replace("[", "")
        val finText = oneText.replace("]", "")
        tvCustomer.text = finText

        if (localTaskCache.contactPhone != null){
            iconTorsoLay.visible(localTaskCache.contactPhone.isNotEmpty())
            tvTroubleUserPhoneText.text = localTaskCache.contactPhone.emptyIfNull()
        }

        if (localTaskCache.comment != null){
            layoutIconLay.visible(localTaskCache.comment.isNotEmpty())
            tvTroubleUserCommentText.text = localTaskCache.comment.emptyIfNull()
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun setTroubleAdditionalInfo(time: Long, lat: Double, lon: Double) {
        val timeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(time)
        tvInfoTrouble.text = "$timeFormat (${lat}, ${lon}) "
    }

    override fun setRecyclerData(list: List<StatusTaskExtended>?) {
        if (list != null){
            adapters.update(list)
            troubleRvUpContainers.adapter = adapters
        }
    }

    override fun showPhoto(model: ArrayList<GarbagePhotoModel>) {
        if (model.isNotEmpty()){
            val adapters = ProblemPhotoAdapter(deleteOption = { photo ->
                presenter.onPhotoDeleteClicked(photo)
            }, photo = {
               presenter.getPhoto(it)
            })
            adapters.update(model.firstOrNull()?.item!!)
            rvTroubleContainersDown.adapter = adapters
            rvTroubleContainersDown.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun setTroubleDropdownList(list: List<TaskFailureReason>) {
        val name = presenter.trouble?.name ?: ""
        val bottomSheetDialogFragment =
            ProblemBottomSheetFragment(name,list, object : ProblemBottomSheetFragment.TroubleListener {
                override fun setOnClickSuccessfully(position: Int) {
                    presenter.onTroubleReasonSelected(position)
                }

                override fun setOnClickComplete(bottomFragment : ProblemBottomSheetFragment) {
                    presenter.confirmButtonClicked(bottomFragment)
                }

                override fun setOnClickFailure() {
                   requireActivity().onBackPressed()
                }
            })
        bottomSheetDialogFragment.isCancelable = false
        bottomSheetDialogFragment.show(
            requireActivity().supportFragmentManager,
            bottomSheetDialogFragment.tag
        )

        actvTroubleReasonDropdownMenu.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item_simple_text,
                list.map { taskFailureReason -> taskFailureReason.name }
            )
        )
        actvTroubleReasonDropdownMenu.setOnItemClickListener { adapterView, view, position, id ->
            presenter.onTroubleReasonSelected(position)
        }
    }

    override fun onResume() {
        super.onResume()
        lastClickTime = 0
    }
}
