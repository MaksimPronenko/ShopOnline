package home.samples.shoponline.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import home.samples.shoponline.R
import home.samples.shoponline.databinding.CatalogItemBinding
import home.samples.shoponline.models.ProductTableWithFavourites

class ProductAdapter(
    val context: Context,
    private val onItemClick: (String) -> Unit,
    private val addToFavorites: (String, Boolean) -> Unit
) : RecyclerView.Adapter<CatalogViewHolder>() {
    private var data: List<ProductTableWithFavourites> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ProductTableWithFavourites>) {
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
                val productImageAdapter = ImageAdapter(
                    context = context,
                    onImageClick = { onItemClick(item.productTable.productDataTable.id) }
                )
                this.productImagePager.adapter = productImageAdapter
                val productImages: MutableList<String> = mutableListOf()
                item.productTable.images.forEach {
                    productImages.add(it.imageURIString)
                }
                productImageAdapter.setData(productImages.toList())
                val oldPriceText =
                    SpannableString("${item.productTable.productDataTable.price} ${item.productTable.productDataTable.unit}")
                oldPriceText.setSpan(StrikethroughSpan(), 0, oldPriceText.length, 0)
                oldPrice.text = oldPriceText
                val newPriceText =
                    "${item.productTable.productDataTable.priceWithDiscount} ${item.productTable.productDataTable.unit}"
                newPrice.text = newPriceText
                val priceDiscountText = "-${item.productTable.productDataTable.discount}%"
                priceDiscount.text = priceDiscountText
                productTitle.text = item.productTable.productDataTable.title
                productSubtitle.text = item.productTable.productDataTable.subtitle
                rating.text = item.productTable.productDataTable.rating.toString()
                val feedbackCountText = "(${item.productTable.productDataTable.feedbackCount})"
                feedbackCount.text = feedbackCountText
                addToFavoritesButton.setImageDrawable(
                    getDrawable(
                        context,
                        if (item.favourite) R.drawable.heart_filled else R.drawable.heart_empty
                    )
                )

                root.setOnClickListener {
                    onItemClick(item.productTable.productDataTable.id)
                }

                addToFavoritesButton.setOnClickListener {
                    addToFavorites(
                        item.productTable.productDataTable.id,
                        !item.favourite
                    )
                }
            }
        }
    }
}

class CatalogViewHolder(val binding: CatalogItemBinding) :
    RecyclerView.ViewHolder(binding.root)