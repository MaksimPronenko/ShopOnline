package home.samples.shoponline.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import home.samples.shoponline.databinding.ProductInfoItemBinding
import home.samples.shoponline.models.InfoPartTable

class ProductInfoAdapter: RecyclerView.Adapter<ProductInfoViewHolder>() {
    private var data: List<InfoPartTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<InfoPartTable>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInfoViewHolder {
        return ProductInfoViewHolder(
            ProductInfoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductInfoViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            if (item != null) {
                infoTitle.text = item.title
                infoValue.text = item.value
            }
        }
    }
}

class ProductInfoViewHolder(val binding: ProductInfoItemBinding) :
    RecyclerView.ViewHolder(binding.root)