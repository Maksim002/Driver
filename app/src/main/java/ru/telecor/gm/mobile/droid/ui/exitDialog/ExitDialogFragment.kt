package ru.telecor.gm.mobile.droid.ui.exitDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_route_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.exitDialog.ExitDialogPresenter
import ru.telecor.gm.mobile.droid.presentation.exitDialog.ExitDialogView
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import toothpick.Toothpick

class ExitDialogFragment : MvpAppCompatDialogFragment(), ExitDialogView {

    companion object {
        fun newInstance() = ExitDialogFragment()
    }

    @InjectPresenter
    lateinit var presenter: ExitDialogPresenter

    @ProvidePresenter
    fun providePresenter(): ExitDialogPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ExitDialogPresenter::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
            .setView(LayoutInflater.from(context).inflate(R.layout.dialog_route_result, null))
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.dialog_route_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        btnExit.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (!ConnectivityUtils.syncAvailability(requireContext())) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            R.string.dialog_route_result_slow_internet_to_connection,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        presenter.onExitButtonClicked()
                    }
                }
            }
        }

        btnTryAgain.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (!ConnectivityUtils.syncAvailability(requireContext())) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            R.string.dialog_route_result_slow_internet_to_connection,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    presenter.onTryAgainButtonClicked()
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun showNumUploadPhoto(
        value: Float,
        loadCount: Int,
        maxCount: Int,
        destroyCount: Int,
        loadStatus: ExitDialogView.LoadStatus
    ) {
        chart_photo.apply() {
            val p = if ((value < 0) or (value > 100)) 100F else value
            if (p != 0.0f) {
                setProgress(p, true)
            }
            setTextFormatter { "$loadCount из $maxCount" }
            if (destroyCount > 0 && loadStatus == ExitDialogView.LoadStatus.FINiSH) {
                recovery.visibility = View.VISIBLE
                text_route_completion.text =
                    resources.getString(R.string.text_route_completion) + destroyCount.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showEstimatedTimeOfUnloading(value: Int, loadStatus: ExitDialogView.LoadStatus) {

        var text = ""
        when (loadStatus) {

            ExitDialogView.LoadStatus.START -> {
                if (value == -1) {
                    text = resources.getString(R.string.estimated_time_not_calculate)
                    progress_estimated.visibility = View.VISIBLE
                } else {
                    val hour = value / 3600
                    val min = value / 60 % 60
                    val sec = value / 1 % 60
                    text = String.format(
                        "%s: %02d:%02d:%02d",
                        resources.getString(R.string.dialog_estimated_time_of_unloading),
                        hour,
                        min,
                        sec
                    )
                    progress_estimated.visibility = View.INVISIBLE
                }
            }
            ExitDialogView.LoadStatus.FINiSH -> {
                text = resources.getString(R.string.estimated_time_all_load)
                progress_estimated.visibility = View.INVISIBLE
            }

            ExitDialogView.LoadStatus.ERROR -> {
                text = resources.getString(R.string.estimated_time_load_error)
                progress_estimated.visibility = View.INVISIBLE
            }

            else -> progress_estimated.visibility = View.INVISIBLE
        }

        dialog_estimated_time_of_unloading.text = text
    }

    override fun showNumUploadRoute(value: Float, loadCount: Int, maxCount: Int) {
        chart_route.apply() {
            val p = if ((value < 0) or (value > 100)) 100F else value
            setProgress(p, true)
            setTextFormatter { "$loadCount из $maxCount" }
        }
    }

    override fun showSuccessDialog() {
        iv_status.setImageResource(R.drawable.ic_success_fill)
        tv_title.text = resources.getString(R.string.dialog_route_result_thanks)
        btnExit.visible(true)
        btnTryAgain.visible(false)
    }

    override fun showFailDialog() {
        iv_status.setImageResource(R.drawable.ic_error_fill)
        tv_title.text = resources.getString(R.string.dialog_route_result_error)
        btnTryAgain.visible(true)
        btnExit.visible(false)
    }

    override fun setLoadingState(value: ExitDialogView.WorkStatus) {
        val clickable = value == ExitDialogView.WorkStatus.ERROR
        btnTryAgain.visible(clickable)
        btnExit.visible(clickable)
        //сменил на постоянный true
//        pbLoading.visible(clickable)
        pbLoading.visible(true)

        when (value) {
            ExitDialogView.WorkStatus.PREPARE -> {
                tv_result.text = getString(R.string.dialog_route_result_data_preparation)
                data_preparation.visibility = View.VISIBLE
            }
            ExitDialogView.WorkStatus.UPLOAD -> {
                tv_result.text = getString(R.string.dialog_route_result_title_loading)
                data_preparation.visibility = View.VISIBLE
            }
            ExitDialogView.WorkStatus.FINISH -> {
                tv_result.text = getString(R.string.dialog_route_result_title_result)
                data_preparation.visibility = View.INVISIBLE
            }
            ExitDialogView.WorkStatus.ERROR -> {
                tv_result.text = getString(R.string.dialog_route_result_route_error)
                data_preparation.visibility = View.INVISIBLE
            }
        }
    }


    override fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showLoadingRouteDialog() {
        iv_status.setImageResource(R.drawable.ic_error_fill)
        tv_title.text = resources.getString(R.string.dialog_route_result_route_completion)
        btnTryAgain.visible(true)
        btnExit.visible(false)
    }

    override fun showDataPreparationDialog() {
        iv_status.setImageResource(R.drawable.ic_error_fill)
        tv_title.text = resources.getString(R.string.dialog_route_result_route_completion)
        btnTryAgain.visible(true)
        btnExit.visible(false)
    }

}