package home.samples.shoponline.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import home.samples.shoponline.R
import home.samples.shoponline.databinding.PagerImageItemBigBinding

class ImageAdapterBig(
    val context: Context
) : RecyclerView.Adapter<ImageBigViewHolder>() {
    private var data: List<String> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageBigViewHolder {
        return ImageBigViewHolder(
            PagerImageItemBigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageBigViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            item?.let {
                val uri = Uri.parse(it)
                Glide
                    .with(pagerImage.context)
                    .load(uri)
                    .placeholder(androidx.constraintlayout.widget.R.color.material_grey_300)
                    .into(pagerImage)
                when (position) {
                    0 -> {
                        circle0.setColorFilter(context.getColor(R.color.pink))
                        circle1.setColorFilter(context.getColor(R.color.grey_circle))
                    }
                    else -> {
                        circle0.setColorFilter(context.getColor(R.color.grey_circle))
                        circle1.setColorFilter(context.getColor(R.color.pink))
                    }
                }
            }
        }
    }
}

class ImageBigViewHolder(val binding: PagerImageItemBigBinding) :
    RecyclerView.ViewHolder(binding.root)