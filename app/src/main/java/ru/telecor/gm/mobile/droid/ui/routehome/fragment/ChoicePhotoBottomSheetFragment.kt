package ru.telecor.gm.mobile.droid.ui.routehome.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_choice_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_choice_bottom_sheet.textMaven
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.LoaderInfo
import ru.telecor.gm.mobile.droid.ui.base.BaseBottomSheetFragment
import ru.telecor.gm.mobile.droid.di.adapters.DropAdapter
import ru.telecor.gm.mobile.droid.di.adapters.DropHolder
import toothpick.Toothpick

class ChoicePhotoBottomSheetFragment(
    var listener: DropHolder.Listener,
    var list: ArrayList<LoaderInfo> = arrayListOf(),
) : BaseBottomSheetFragment(), ChoicePhotoBottomSheetView {

    override val layoutRes = R.layout.fragment_choice_bottom_sheet

    @InjectPresenter
    lateinit var presenter: ChoicePhotoBottomSheetPresenter

    @ProvidePresenter
    fun providePresenter(): ChoicePhotoBottomSheetPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(ChoicePhotoBottomSheetPresenter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initRecyclerView()
    }

    private fun initClick() {
        clearTxtBtn.setOnClickListener {
            textMaven.setText("")
        }
    }

    fun update(item: ArrayList<LoaderInfo>) {
        list = item
    }

    private fun initRecyclerView() {
        val adapters = DropAdapter(object : DropHolder.Listener {
            override fun setOnClickListener(item: LoaderInfo) {
                listener.setOnClickListener(item)
                dismiss()
            }
        })

        textMaven.addTextChangedListener {
            if (textMaven.text.toString() != "") {
                adapters.filter(it, list)
            } else {
                adapters.update(list)
            }
        }

        adapters.update(list)
        recyclerView.adapter = adapters
    }

 override fun getTheme(): Int {
    return if(Build.VERSION.SDK_INT > 22){
        R.style.AppBottomSheetDialogTheme
    }else{
        0
    }
}
}