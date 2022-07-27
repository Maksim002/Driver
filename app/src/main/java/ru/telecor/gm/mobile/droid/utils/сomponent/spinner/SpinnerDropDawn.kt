package ru.telecor.gm.mobile.droid.utils.сomponent.spinner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder
import ru.telecor.gm.mobile.droid.R

//DavenSpinner
class SpinnerDropDawn(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val layout: LinearLayout
    private val label: AppCompatImageView
    private val text: TextView
    private val hide: TextView
    private val image: ImageView
    private val recycler: RecyclerView
    private lateinit var adapters: SpinnerAdapter
    private var list: ArrayList<SpinnerModel> = arrayListOf()
    var clickItemListener = MutableLiveData<SpinnerModel>()
    private var validValue = true

    init {
        inflate(context, R.layout.spinner_drop_dawn, this)

        layout = findViewById(R.id.layoutSpin)
        label = findViewById(R.id.labelHidden)
        text = findViewById(R.id.testView)
        hide = findViewById(R.id.textHide)
        image = findViewById(R.id.imageView)
        recycler = findViewById(R.id.recyclerView)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DownSpinner)
//        hide.text = attributes.getText(0)
        text.text = attributes.getText(0)
        attributes.recycle()

        initRv()
    }

    private fun initRv() {
        adapters = SpinnerAdapter(object : SpinnerAdapter.Listener {
            override fun setOnClickSpinner(item: SpinnerModel) {
                clickItemListener.value = item
                text.text = item.text
                list.clear()
                recycler.visible(list.isNotEmpty())
                setMark(R.drawable.ic_baseline_arrow_drop_down_24)
//                hide.visible(text.text.isNotEmpty())
//                image.visible(text.text.isNotEmpty())
            }
        })
    }

    //new
    fun <T> setUpdate(id: T, text: T, image: T) {
        if (validValue){
            list.add(SpinnerModel(id.toString(), text.toString(), image.toString()))
            returnVal()
        }
    }
    fun setValidation() {
        if (recycler.isVisible){
            validValue = false
            setMark(R.drawable.ic_baseline_arrow_drop_down_24)
            list.clear()
            recycler.visible(list.isNotEmpty())
        }else{
            validValue = true
            setMark(R.drawable.ic_baseline_arrow_drop_up_24)
        }
    }

    //old
    fun setOnClick(item: ArrayList<SpinnerModel>) {
        if (recycler.isVisible){
            setMark(R.drawable.ic_baseline_arrow_drop_down_24)
            list.clear()
            recycler.visible(list.isNotEmpty())
        }else{
            setMark(R.drawable.ic_baseline_arrow_drop_up_24)
            list = item
            returnVal()
        }
    }

    fun setMark(mark: Int){
        label.setImageDrawable(ContextCompat.getDrawable(context, mark))
    }

    fun defaultValue(name: String){
        if (name != "null"){
            text.text = name
        }
    }

    private fun returnVal(){
        adapters.update(list)
        recycler.visible(list.isNotEmpty())
        recycler.adapter = adapters
    }



    fun View.visible(visible: Boolean, invisibleState: Int = View.GONE) {
        visibility = if (visible) View.VISIBLE else invisibleState
    }
}

//Adapter
class SpinnerAdapter(var listener: Listener, item: ArrayList<SpinnerModel> = arrayListOf()) :
    GenericRecyclerAdapter<SpinnerModel>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_spinner)
    }

    override fun bind(item: SpinnerModel, holder: ViewHolder) = with(holder.itemView) {
        this.findViewById<TextView>(R.id.textSpinner).text = item.text
        this.findViewById<LinearLayout>(R.id.layoutItem).setOnClickListener {
            listener.setOnClickSpinner(item)
        }
    }

    interface Listener {
        fun setOnClickSpinner(item: SpinnerModel)
    }
}

//Model
class SpinnerModel(
    var id: String,
    var text: String,
    var image: String
)