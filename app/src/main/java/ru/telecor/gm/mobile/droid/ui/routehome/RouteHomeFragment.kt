package ru.telecor.gm.mobile.droid.ui.routehome

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_route_home.*
import kotlinx.android.synthetic.main.layout_general_on.*
import kotlinx.android.synthetic.main.layout_general_tw.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.di.adapters.DropHolder
import ru.telecor.gm.mobile.droid.entities.DriverInfo
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.entities.RouteInfo
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.presentation.routehome.RouteHomePresenter
import ru.telecor.gm.mobile.droid.presentation.routehome.RouteHomeView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.routehome.fragment.ChoicePhotoBottomSheetFragment
import ru.telecor.gm.mobile.droid.utils.PhotoUtils.stringToObject
import toothpick.Toothpick
import kotlin.collections.ArrayList

class RouteHomeFragment : BaseFragment(), RouteHomeView {

    companion object {

        fun newInstance() = RouteHomeFragment()
    }

    override val layoutRes = R.layout.fragment_route_home

    @InjectPresenter
    lateinit var presenter: RouteHomePresenter

    @ProvidePresenter
    fun providePresenter(): RouteHomePresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(RouteHomePresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRollbackRoute.setOnClickListener {
            presenter.onRollbackButtonClicked()
        }

        btnToRoute.setOnClickListener { presenter.onRouteButtonClicked() }

        gitGreyColor(requireContext())
    }

    override fun setImProfile(boolean: Boolean, stream: Bitmap?) {
        if (stream.toString() != "[size=0].inputStream()") {
            if (boolean && stream != null) {
                image_profile.setImageBitmap(stream)
            } else {
                image_profile.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile,
                    null))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setDriverInfo(driverInfo: DriverInfo) {
        tvDriverName.text =
            "${driverInfo.lastName.emptyIfNull()} ${driverInfo.firstName.emptyIfNull()} ${driverInfo.middleName.emptyIfNull()}"
    }

    @SuppressLint("SetTextI18n")
    override fun setRouteInfo(routeInfo: RouteInfo) {
        tvVehicleModel.text = routeInfo.unit.vehicle.model
        tvVehicleRegNumber.text = routeInfo.unit.vehicle.regNumber
        setDriverInfo(routeInfo.unit.driver)
    }

    override fun setFirstLoaderList(list: List<LoaderInfo>) {
        layoutGeneralOne.setOnClickListener(200) {
            val bottomSheetDialogFragment =
                ChoicePhotoBottomSheetFragment(object : DropHolder.Listener {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    override fun setOnClickListener(item: LoaderInfo) = with(layoutGeneralOne) {
                        presenter.nameValue.value = item
                        layoutLayO.isVisible = true
                        usedLayO.isVisible = false
                        if (item.photo.toString() != "null") {
                            imageProfileO.setImageBitmap(stringToObject(item.photo!!.content.toString()))
                        } else {
                            imageProfileO.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile,
                                null))
                        }
                        val first = item.firstName
                        val last = item.lastName
                        if (item.middleName != null) {
                            val middle = item.middleName
                            titleTxtO.text = "$first $last $middle"
                        } else {
                            titleTxtO.text = "$first $last"
                        }
                        descriptionTextO.text = item.employeeId
                        presenter.onFirstLoaderSelected(item)

                        clearBtnO.setOnClickListener {
                            layoutLayO.isVisible = false
                            usedLayO.isVisible = true
                            layoutLayT.isVisible = false
                            usedLayT.isVisible = true
                            gitGreyColor(context)
                        }
                        gitBlueColor(context)
                    }
                })
            bottomSheetDialogFragment.update(list as ArrayList<LoaderInfo>)
            bottomSheetDialogFragment.show(requireActivity().supportFragmentManager,
                bottomSheetDialogFragment.tag)
        }
    }

    override fun setSecondLoaderList(list: List<LoaderInfo>) {
        layoutGeneralTwo.setOnClickListener(200) {
            if (layoutLayO.isVisible) {
                val bottomSheetDialogFragment =
                    ChoicePhotoBottomSheetFragment(object : DropHolder.Listener {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        override fun setOnClickListener(item: LoaderInfo) = with(layoutGeneralTwo) {
                            layoutLayT.isVisible = true
                            usedLayT.isVisible = false
                            gitBlueColor(context)
                            if (item.photo.toString() != "null") {
                                imageProfileT.setImageBitmap(stringToObject(item.photo!!.content.toString()))
                            } else {
                                imageProfileT.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile,
                                    null))
                            }
                            val first = item.firstName
                            val last = item.lastName
                            if (item.middleName != null) {
                                val middle = item.middleName
                                titleTxtT.text = "$first $last $middle"
                            } else {
                                titleTxtT.text = "$first $last"
                            }
                            descriptionTextT.text = item.employeeId
                            presenter.onFirstLoaderSelected(item)

                            clearBtnT.setOnClickListener {
                                layoutLayT.isVisible = false
                                usedLayT.isVisible = true
                            }
                        }
                    })
                bottomSheetDialogFragment.update(list as ArrayList<LoaderInfo>)
                bottomSheetDialogFragment.show(requireActivity().supportFragmentManager,
                    bottomSheetDialogFragment.tag)
            }
        }
    }

    fun gitGreyColor(context: Context) {
        layoutTwo.background =
            ContextCompat.getDrawable(context, R.drawable.circle_background_layout_grey)
        imSearchT.setColorFilter(ContextCompat.getColor(context, R.color.gray))
        textMavenT.setHintTextColor(ContextCompat.getColor(context, R.color.gray))
        textHimTwo.setTextColor(ContextCompat.getColor(context, R.color.gray))
    }

    fun gitBlueColor(context: Context) {
        layoutTwo.background =
            ContextCompat.getDrawable(context, R.drawable.circle_background_layout_drop)
        imSearchT.setColorFilter(ContextCompat.getColor(context, R.color.color_text_light_blue))
        textMavenT.setHintTextColor(ContextCompat.getColor(context, R.color.color_text_light_blue))
        textHimTwo.setTextColor(ContextCompat.getColor(context, R.color.color_text_light_blue))
    }

    override fun showNeedPermissionMessage() {
        showMessage(resources.getString(R.string.error_permission_denied))
    }

    override fun setLoadingState(value: Boolean) {
        if (value) {
            pbLoading.visibility = View.VISIBLE
        } else {
            pbLoading.visibility = View.INVISIBLE
            conOne.isVisible = !value
            conTwo.isVisible = !value
        }
    }
}
