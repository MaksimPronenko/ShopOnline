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
import home.samples.shoponline.models.ProductTable


class ProductAdapter(
    val context: Context,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CatalogViewHolder>() {
    private var data: List<ProductTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ProductTable>) {
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
                val oldPriceText = SpannableString("${item.productDataTable.price} ${item.productDataTable.unit}")
                oldPriceText.setSpan(StrikethroughSpan(), 0, oldPriceText.length, 0)
                oldPrice.text = oldPriceText
                val newPriceText = "${item.productDataTable.priceWithDiscount} ${item.productDataTable.unit}"
                newPrice.text = newPriceText
                val priceDiscountText = "-${item.productDataTable.discount}%"
                priceDiscount.text = priceDiscountText
                productTitle.text = item.productDataTable.title
                productSubtitle.text = item.productDataTable.subtitle
                rating.text = item.productDataTable.rating.toString()
                val feedbackCountText = "(${item.productDataTable.feedbackCount})"
                feedbackCount.text = feedbackCountText
                val uri = Uri.parse(item.images[0].imageURIString)
                Glide
                    .with(image.context)
                    .load(uri)
                    .into(image)
            }
        }
        holder.binding.root.setOnClickListener {
            if (item != null) {
                onItemClick(item.productDataTable.id)
            }
        }
    }
}

class CatalogViewHolder(val binding: CatalogItemBinding) :
    RecyclerView.ViewHolder(binding.root)