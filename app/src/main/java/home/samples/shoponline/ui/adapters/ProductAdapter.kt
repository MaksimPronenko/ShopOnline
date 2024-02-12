package home.samples.shoponline.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import home.samples.shoponline.databinding.CatalogItemBinding
import home.samples.shoponline.models.Product


class ProductAdapter(
    val context: Context,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CatalogViewHolder>() {
    private var data: List<Product> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Product>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        return CatalogViewHolder(
            CatalogItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            if (item != null) {
                val oldPriceText = SpannableString("${item.price.price} ${item.price.unit}")
                oldPriceText.setSpan(StrikethroughSpan(), 0, oldPriceText.length, 0)
                oldPrice.text = oldPriceText
                val newPriceText = "${item.price.priceWithDiscount} ${item.price.unit}"
                newPrice.text = newPriceText
                val priceDiscountText = "-${item.price.discount}%"
                priceDiscount.text = priceDiscountText
                productTitle.text = item.title
                productSubtitle.text = item.subtitle
                rating.text = item.feedback.rating.toString()
                val feedbackCountText = "(${item.feedback.count})"
                feedbackCount.text = feedbackCountText
//                val uri = Uri.parse("android.resource://home.samples.shoponline/res/drawable/image_name")
//                val res: Resources = context.getResources()
//                val resUri: Uri = getUriToResource(res, "dra")
                val uri = Uri.parse("android.resource://"+context.packageName +"/drawable/product_1_image_1")
                Glide
                    .with(image.context)
                    .load(uri)
                    .into(image)
            }
        }
        holder.binding.root.setOnClickListener {
            if (item != null) {
                onItemClick(item.id)
            }
        }
    }
}

class CatalogViewHolder(val binding: CatalogItemBinding) :
    RecyclerView.ViewHolder(binding.root)