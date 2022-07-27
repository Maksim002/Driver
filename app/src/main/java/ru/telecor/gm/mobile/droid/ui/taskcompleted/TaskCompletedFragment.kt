package ru.telecor.gm.mobile.droid.ui.taskcompleted

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhaarman.supertooltips.ToolTip
import kotlinx.android.synthetic.main.fragment_task_completed.*
import kotlinx.android.synthetic.main.fragment_task_completed.addressMessage
import kotlinx.android.synthetic.main.fragment_task_completed.iconTorsoLay
import kotlinx.android.synthetic.main.fragment_task_completed.layoutIconLay
import kotlinx.android.synthetic.main.fragment_task_completed.layoutTask
import kotlinx.android.synthetic.main.fragment_task_completed.messageCon
import kotlinx.android.synthetic.main.fragment_task_completed.pbLoading
import kotlinx.android.synthetic.main.fragment_task_completed.troubleArrowBack
import kotlinx.android.synthetic.main.fragment_task_completed.troubleContainerTask
import kotlinx.android.synthetic.main.fragment_task_completed.troubleRvUpContainers
import kotlinx.android.synthetic.main.fragment_task_completed.troubleTaskDetailsIm
import kotlinx.android.synthetic.main.fragment_task_completed.troubleTvAddress
import kotlinx.android.synthetic.main.fragment_task_completed.tvCustomer
import kotlinx.android.synthetic.main.fragment_task_completed.tvTroubleFactTimeText
import kotlinx.android.synthetic.main.fragment_task_completed.tvTroublePreferredTimeText
import kotlinx.android.synthetic.main.fragment_task_completed.tvTroublePriority
import kotlinx.android.synthetic.main.fragment_task_completed.tvTroubleUserCommentText
import kotlinx.android.synthetic.main.fragment_task_completed.tvTroubleUserPhoneText
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedPresenter
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.taskcompleted.rv.TaskCompletedAdapter
import ru.telecor.gm.mobile.droid.ui.taskcompleted.rv.TaskCompletedPhotoAdapter
import ru.telecor.gm.mobile.droid.utils.millisecondsDate
import toothpick.Toothpick
import java.text.SimpleDateFormat
import java.util.*

class TaskCompletedFragment : BaseFragment(), TaskCompletedView {

    private var setListTask: List<TaskRelations> = arrayListOf()
    private var setTaskId = 0
    private var addressTxt = ""
    private var photoVisibility = false

    companion object {
        private const val TASK_ID_KEY = "taskid"

        fun newInstance(taskId: Int, listTask: List<TaskRelations>) =
            TaskCompletedFragment().apply {
                arguments = Bundle().apply {
                    putInt(TASK_ID_KEY, taskId)
                    setTaskId = taskId
                }
                setListTask = listTask
            }
    }

    override val layoutRes = R.layout.fragment_task_completed

    @InjectPresenter
    lateinit var presenter: TaskCompletedPresenter

    @ProvidePresenter
    fun providePresenter(): TaskCompletedPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(TaskCompletedPresenter::class.java)
            .apply { taskId = arguments?.getInt(TASK_ID_KEY) ?: -1 }
    }

    private var beforeRecyclerAdapter = TaskCompletedPhotoAdapter { photo ->
        presenter.getPhoto(photo)
        photoVisibility = false
    }
    private var afterRecyclerAdapter = TaskCompletedPhotoAdapter{ photo ->
        presenter.getPhoto(photo)
        photoVisibility = false
    }
    private var troubleRecyclerAdapter = TaskCompletedPhotoAdapter{ photo ->
        presenter.getPhoto(photo)
        photoVisibility = false
    }
    private var troubleTaskRecyclerAdapter = TaskCompletedPhotoAdapter{ photo ->
        presenter.getPhoto(photo)
        photoVisibility = false
    }

    private var adapters = TaskCompletedAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        setItemTask(setListTask, setTaskId)

        rvBeforePhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvBeforePhotos.adapter = beforeRecyclerAdapter

        rvAfterPhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvAfterPhotos.adapter = afterRecyclerAdapter

        rvTroublePhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTroublePhotos.adapter = troubleRecyclerAdapter

        rvTroubleTaskPhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTroubleTaskPhotos.adapter = troubleTaskRecyclerAdapter
    }

    private fun initClick() {
        troubleArrowBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        troubleBtnSettings.setOnClickListener {
            presenter.onSettingsClicked()
        }

        //Всплывающее сообщение с адресом
        troubleTvAddress.setOnClickListener {
            if (addressTxt != "") {
                val toolTip = ToolTip()
                    .withText(addressTxt)
                    .withColor(resources.getColor(R.color.gray2))

                val message = addressMessage.showToolTipForView(toolTip, addressMessage)
                Handler().postDelayed({
                    message.remove()
                }, 2500)
            } else if (addressTxt == "") {
                showMessage("Адрес пусть")
            }
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

        iconTorsoLay.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${tvTroubleUserPhoneText.text}")
                startActivity(intent)
            }
    }

    override fun showSettingsMenu() {
        val bottomSheetDialogFragment = SettingBottomSheetFragment()
        bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun setAddress(str: String) {
        addressTxt = str
        troubleTvAddress.text = str
    }

    override fun setContainersList(list: List<TaskItemPreviewData>) {
        adapters.update(list)
        troubleRvUpContainers.adapter = adapters
    }

    private fun setItemTask(listTask: List<TaskRelations>, taskId: Int) {
        if (listTask.isNotEmpty() && taskId.toString().isNotEmpty()) {
            val task = listTask.first { it.task.id == taskId }

            if (task.task.priority != null) {
                messageCon.visible(task.task.priority.name.isNotEmpty())
                tvTroublePriority.text = task.task.priority.name ?: ""
            }

            tvTroublePreferredTimeText.text = SimpleDateFormat(
                "E HH:mm",
                Locale("ru")
            ).format(Date(task.task.preferredTimeStart))
                .capitalize() + " - " + SimpleDateFormat(
                "E HH:mm", Locale("ru")
            ).format(Date(task.task.preferredTimeEnd)).capitalize()

            tvTroubleFactTimeText.text = millisecondsDate(task.task.planTimeStart)

            val text = task.task.taskItems.map { item -> item.customer.name }
            val oneText = text.toString().replace("[", "")
            val finText = oneText.replace("]", "")
            tvCustomer.text = finText

            if (task.task.contactPhone != null) {
                iconTorsoLay.visible(task.task.contactPhone.isNotEmpty())
                tvTroubleUserPhoneText.text = task.task.contactPhone.emptyIfNull()
            }

            if (task.task.comment != null) {
                layoutIconLay.visible(task.task.comment.isNotEmpty())
                tvTroubleUserCommentText.text = task.task.comment.emptyIfNull()
            }

            statusText.text = presenter.getStatus(task.task.statusType)

            when (presenter.getStatus(task.task.statusType)) {
                "Успешно" -> {
                    statusIm.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_mark))
                    statusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_color))
                    statusText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey_color_alpha))
                }
                "Частично успешно" -> {
                    statusIm.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_oreng_marker))
                    statusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_color))
                    statusText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow_color_alpha))
                }
                "Невывоз" -> {
                    statusIm.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_non_pickup))
                    statusText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_to_red_color))
                    statusText.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_to_red_color_alpha))
                }
            }
        }
    }

    override fun showListOfBeforePhoto(list: List<ProcessingPhoto>) {
        beforeRecyclerAdapter.setList(list)
        tvBefore.visible(list.isNotEmpty())
        rvBeforePhotos.visible(list.isNotEmpty())
        if (!photoVisibility && list.isNotEmpty()){
            photoVisibility = true
            resConLay.visible(list.isNotEmpty())
            layoutTaskCon.visible(list.isEmpty())
        }
    }

    override fun showListOfAfterPhoto(list: List<ProcessingPhoto>) {
        afterRecyclerAdapter.setList(list)
        tvAfter.visible(list.isNotEmpty())
        rvAfterPhotos.visible(list.isNotEmpty())
        validation(list)
    }

    override fun showListOfTroublePhoto(list: List<ProcessingPhoto>) {
        troubleRecyclerAdapter.setList(list)
        tvTrouble.visible(list.isNotEmpty())
        rvTroublePhotos.visible(list.isNotEmpty())
        validation(list)
    }

    override fun showListOfTroubleTaskPhoto(list: List<ProcessingPhoto>) {
        troubleTaskRecyclerAdapter.setList(list)
        tvTroubleTask.visible(list.isNotEmpty())
        rvTroubleTaskPhotos.visible(list.isNotEmpty())
        validation(list)
    }

    private fun validation(list: List<ProcessingPhoto>){
        if (!photoVisibility && list.isNotEmpty()){
            photoVisibility = true
            resConLay.visible(list.isNotEmpty())
            layoutTaskCon.visible(list.isEmpty())
        }
    }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value)
    }
}
