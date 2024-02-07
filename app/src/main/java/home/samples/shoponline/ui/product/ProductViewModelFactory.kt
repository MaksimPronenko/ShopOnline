package home.samples.shoponline.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory (private val productViewModel: ProductViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return productViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}