package ru.telecor.gm.mobile.droid.ui.routestands

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_route_stands.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.extensions.color
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.routestands.RouteStandsPresenter
import ru.telecor.gm.mobile.droid.presentation.routestands.RouteStandsView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.login.fragment.setting.settingFunctionality.SettingBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestands.fragment.ConfirmationBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestands.fragment.SelectPolygonBottomSheetFragment
import ru.telecor.gm.mobile.droid.ui.routestands.fragment.rv.ListPolygonsAdapter
import ru.telecor.gm.mobile.droid.ui.routestands.rv.RouteStandAdapter
import ru.telecor.gm.mobile.droid.ui.utils.LocationUtils
import ru.telecor.gm.mobile.droid.utils.ConnectivityUtils
import toothpick.Toothpick
import kotlin.collections.ArrayList
import androidx.activity.OnBackPressedCallback
import kotlinx.android.synthetic.main.item_base_tool_bar.*

class RouteStandsFragment : BaseFragment(), RouteStandsView {

    private var valueCheck = false

    private var visibility = true

    private var listWindow: List<TaskRelations> = arrayListOf()

    companion object {

        fun newInstance() = RouteStandsFragment()
    }

    override val layoutRes = R.layout.fragment_route_stands

    @InjectPresenter
    lateinit var presenter: RouteStandsPresenter

    @ProvidePresenter
    fun providePresenter(): RouteStandsPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(RouteStandsPresenter::class.java)
    }

    private var recyclerAdapter = RouteStandAdapter { presenter.taskFromListChosen(it) }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {} }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


        btnCurrentTask.setOnClickListener {
            presenter.currentTaskButtonClicked()
        }

        // Обновление списка задая
        btnRefreshTaskList.setOnClickListener {
            presenter.refreshTaskList()
        }

        //Открытие меню
        btnSettings.setOnClickListener { presenter.onSettingsClicked() }

        //Откат нразад
        btnReturnButton.setOnClickListener { presenter.currentTaskButtonClicked() }

        initWindowVisibility(arrayListOf(searchNoView, imgInternetIndicator))

        //Закррытие блока (параметры телефона)
        searchNoView.setOnClickListener {
            imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
            layoutComponents.isVisible = false
            searchNoView.isVisible = false
            searchView.isVisible = true
        }

        //Блок отоброжает параметры телефона
        imgInternetIndicator.setOnClickListener {
            if (!layoutComponents.isVisible) {
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.colorAccent))
                layoutComponents.isVisible = true
                searchNoView.isVisible = true
                searchView.isVisible = false
            } else {
                imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
                layoutComponents.isVisible = false
                searchNoView.isVisible = false
                searchView.isVisible = true
            }
        }

        //Задание
        btnCurrentTask.setOnClickListener { presenter.currentTaskButtonClicked() }

        //Разгруска
        btnDumping.setOnClickListener {
            it.isEnabled = false
            if (ConnectivityUtils.syncAvailability(
                    requireContext(),
                    ConnectivityUtils.DataType.FILE
                )
            ) {
                presenter.dumpingButtonClicked()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Для запроса на подтверждение выгрузки, необходим более скоростной интернет!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //Отоброжение точек
        btnNearStands.setOnClickListener {
            if (valueCheck) {
                valueCheck = false
                initOpeningLogic()
            } else {
                valueCheck = true
                initOpeningLogic()
            }
        }

        //Повторное заполнение облостей видимости полей
        windowVisibility.setOnClickListener {
            val list: ArrayList<TaskRelations> = arrayListOf()
            visibility = visibility != true
            listWindow.forEach {
                list.add(
                    TaskRelations(
                        task = it.task,
                        taskProcessingResult = it.taskProcessingResult,
                        windowVisibility = visibility
                    )
                )
            }
            presenter.viewState.showInternetConnection(visibility)
            recyclerAdapter.updateWindow(list)
        }


        rvTasks.adapter = recyclerAdapter

        ///Поисковик элементов
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(string: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(string: String?): Boolean {
                presenter.onSearchQueryChanged(string)
                imgInternetIndicator.visibility =
                    if (string?.length!! > 0) View.GONE else View.VISIBLE
//                btnNearStands.visibility = if (string.isNotEmpty()) View.GONE else View.VISIBLE
                btnSettings.visibility = if (string.isNotEmpty()) View.GONE else View.VISIBLE
                return false
            }
        })

    }

    //Логиго тотоброжение полей
    private fun initWindowVisibility(view: ArrayList<ImageView>) {
        view.forEach {
            it.setOnClickListener {
                if (!layoutComponents.isVisible) {
                    imgInternetIndicator.setColorFilter(requireActivity().color(R.color.colorAccent))
                    layoutComponents.isVisible = true
                    searchNoView.isVisible = true
                    searchView.isVisible = false
                } else {
                    imgInternetIndicator.setColorFilter(requireActivity().color(R.color.gmm_white))
                    layoutComponents.isVisible = false
                    searchNoView.isVisible = false
                    searchView.isVisible = true
                }
            }
        }
    }

    private fun initOpeningLogic() {
        presenter.nearStandsToggleButtonPressed(
            valueCheck,
            LocationUtils.getBestLocation(requireContext())?.latitude ?: 0.0,
            LocationUtils.getBestLocation(requireContext())?.longitude ?: 0.0
        )
    }

    override fun setTasksList(list: List<TaskRelations>, item: ArrayList<GetListCouponsModel>?) {
        list.forEach {
            if (it.task.id == presenter.taskId)
                it.isEdit = presenter.isEdit
        }
        listWindow = list
        presenter.routes()?.let { recyclerAdapter.routesInfo(it) }
        if (item != null)
            recyclerAdapter.setCoupons(item)
        recyclerAdapter.setList(list)
    }

    override fun showLoading(show: Boolean) {
        if (show)
            alertDialog.show()
        else
            alertDialog.hide()
    }

    override fun scrollRvTo(position: Int) {
        rvTasks.scrollToPosition(position)
    }

    override fun setEmptyListState(value: Boolean) {
        tvEmptyListWarning.visible(value)
        conLay.visible(!value)
    }

    override fun showInternetConnection(value: Boolean) {
        if (value) {
            windowVisibility.setImageResource(R.drawable.ic_see)
        } else {
            windowVisibility.setImageResource(R.drawable.ic_no_see)
        }
    }

    //Открытие настроек
    override fun showSettingsMenu(boolean: Boolean) {
        presenter.isVisibilityNext(View.VISIBLE)
        if (boolean) {
            val bottomSheetDialogFragment = SettingBottomSheetFragment()
            bottomSheetDialogFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag
            )
            presenter.viewState.showSettingsMenu(false)
        }
    }

    override fun showCurrentTime(string: String) {
        tvClock.text = string
    }

    override fun showBatteryLevel(string: String) {
        tvBattery.text = string
    }

    override fun showDumpingConfirmationDialog() {
        presenter.dumpingConfirmed()
    }

    //Выбор палигона
    override fun showPolygonSelectionDialog(list: List<VisitPoint>) {
        val bottomSheetDialogFragment =
            SelectPolygonBottomSheetFragment(list, object : ListPolygonsAdapter.Listener {
                override fun setOnClickPolygons(item: VisitPoint) {
                    presenter.onPolygonSelected(item)
                }
            })
        bottomSheetDialogFragment.isCancelable = false
        bottomSheetDialogFragment.show(
            requireActivity().supportFragmentManager,
            bottomSheetDialogFragment.tag
        )
    }

    override fun enableButton(enable: Boolean) {
        btnDumping.isEnabled = enable
    }

    private val batteryBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            presenter.updateBatteryLevel(level.toString() + "%")
        }
    }

    // Потверждение выбора полигона
    override fun showPolygonEnsureDialog(visitPoint: VisitPoint) {
        val bottomSheetDialogFragment =
            ConfirmationBottomSheetFragment("Полигон ${visitPoint.name} \nВы уверены?",
                object : ConfirmationBottomSheetFragment.Listener {
                    override fun setOnClickYes() {
                        presenter.onPolygonEnsured(visitPoint)
                    }
                    override fun setOnClickNo() {
                        presenter.onPolygonEnsureDialogCancelled()
                    }
                })
        bottomSheetDialogFragment.isCancelable = false
        bottomSheetDialogFragment.show(requireActivity().supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    //Откат
    override fun goBackToTask() {
        activity?.onBackPressed()
    }
}
