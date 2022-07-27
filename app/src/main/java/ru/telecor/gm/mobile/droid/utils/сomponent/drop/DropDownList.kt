package ru.telecor.gm.mobile.droid.utils.сomponent.drop

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.drop_down_list.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.adapters.DropHolder
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.presentation.routestart.RouteStartPresenter
import ru.telecor.gm.mobile.droid.ui.routehome.fragment.ChoicePhotoBottomSheetFragment
import ru.telecor.gm.mobile.droid.utils.PhotoUtils

@SuppressLint("ResourceType")
class DropDownList(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {
    lateinit var fragment: Fragment
    lateinit var presenter: RouteStartPresenter
    private var list: ArrayList<LoaderInfo> = arrayListOf()

    init {
        inflate(context, R.layout.drop_down_list, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DropDownList)
        layoutLay.background = attributes.getDrawable(0)
        textHide.text = attributes.getText(1)
        textHide.setTextColor(attributes.getColor(2, 2))
        attributes.recycle()

    }

    fun defaultUpdate(defaultName: LoaderInfo){
        if (defaultName.id != "9999") {
            var first = ""
            var last = ""
            var middle = ""

            displayingFieldsLay.isVisible = true
            textHide.isVisible = false

            if (defaultName.firstName != null) {
                first = defaultName.firstName
            }
            if (defaultName.lastName != null) {
                last = defaultName.lastName!!
            }
            if (defaultName.middleName != null) {
                middle = defaultName.middleName
            }

            titleTxtO.text = "$first $last $middle"
            descriptionTextO.text = defaultName.employeeId

            if (defaultName.photo.toString() != "null") {
                imageProfileO.setImageBitmap(PhotoUtils.stringToObject(defaultName.photo!!.content.toString()))
            } else {
                imageProfileO.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_image_profile,
                        null
                    )
                )
            }
        }else{
            imageProfileO.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_image_profile,
                    null
                )
            )
            displayingFieldsLay.isVisible = false
            textHide.isVisible = true
        }
    }

    fun update(item: ArrayList<LoaderInfo>, number: Int){
//        list = item
        list.clear()
        list.add(LoaderInfo("00", "9999", "Нет", "грузчика", "", null))
        item.forEach {list.add(it)}
        initBottomFragment(list, number)
    }

    private fun initBottomFragment(item: ArrayList<LoaderInfo>, number: Int) {
        var first = ""
        var last = ""
        var middle = ""

        val bottomSheetDialogFragment = ChoicePhotoBottomSheetFragment(object : DropHolder.Listener {
            override fun setOnClickListener(item: LoaderInfo) {
                if (item.id != "9999"){
                    displayingFieldsLay.isVisible = true
                    textHide.isVisible = false
                    if (number == 1){
                        presenter.namefir.value = item
                    }else{
                        presenter.nameSec.value = item
                    }

                    if (item.photo.toString() != "null") {
                        imageProfileO.setImageBitmap(PhotoUtils.stringToObject(item.photo!!.content.toString()))
                    } else {
                        imageProfileO.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile, null))
                    }
                    if (item.firstName != null){
                        first = item.firstName
                    }
                    if (item.lastName != null){
                        last = item.lastName!!
                    }
                    if (item.middleName != null){
                        middle = item.middleName
                    }
                    titleTxtO.text = "$first $last $middle"
                    descriptionTextO.text = item.employeeId
                    presenter.onFirstLoaderSelected(item, number)
                }else{
                    imageProfileO.setImageDrawable(resources.getDrawable(R.drawable.ic_image_profile, null))
                    displayingFieldsLay.isVisible = false
                    textHide.isVisible = true
                    presenter.displayingWindow = false
                    val model = LoaderInfo("", "", "", "", "", null)
                    if (number == 1){
                        presenter.namefir.value = item
                    }else{
                        presenter.nameSec.value = item
                    }
                    presenter.onFirstLoaderSelected(model, number)
                }
            }
        })
        bottomSheetDialogFragment.update(item)
        bottomSheetDialogFragment.show(fragment.requireActivity().supportFragmentManager,
            bottomSheetDialogFragment.tag)
    }
}