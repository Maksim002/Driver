package ru.telecor.gm.mobile.droid.ui.viewingPhoto

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_viewing_photo.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import android.widget.LinearLayout
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.core.content.ContextCompat


class ViewingPhotoFragment(var photo: ProcessingPhoto? = null) : BaseFragment() {

    private val wrap = LinearLayout.LayoutParams.WRAP_CONTENT
    private val math = LinearLayout.LayoutParams.MATCH_PARENT

    companion object {
        fun newInstance(photo: ProcessingPhoto) = ViewingPhotoFragment(photo)
    }

    override val layoutRes = R.layout.fragment_viewing_photo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isStartPhoto()
        initClick()
    }

    private fun isStartPhoto() {
        if (photo != null) {
            Glide.with(requireActivity())
                .load(photo?.photoPath)
                .into(itemViewingPhoto)
        } else {
            showMessage("При открытии фото что то пошло не так.")
        }

        if (sizePhoto().height < sizePhoto().width) {
            itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                math,
                wrap
            )
        } else {
            itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                wrap,
                math
            )
        }
    }

    private fun initClick() {
        onBeckPress.startAnimation(bottomTurn())

        onBeckPress.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    //Вращает кнопку
    private fun bottomTurn(): RotateAnimation {
        val rotate = RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        return rotate
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            generalLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.circle_background_layout)
            if (sizePhoto().height > sizePhoto().width) {
                itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                    wrap,
                    math
                )
            } else {
                itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                    math,
                    math
                )
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            generalLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.circle_layout_common)
            if (sizePhoto().height < sizePhoto().width) {
                itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                    math,
                    wrap
                )
            } else {
                itemViewingPhoto.layoutParams = LinearLayout.LayoutParams(
                    math,
                    math
                )
            }
        }
    }

    private fun sizePhoto(): Bitmap{
        var photos: Bitmap? = null
        photos = BitmapFactory.decodeFile(photo?.photoPath)
        return photos
    }
}