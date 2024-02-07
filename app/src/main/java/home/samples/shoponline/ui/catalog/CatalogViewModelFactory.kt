package home.samples.shoponline.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CatalogViewModelFactory (private val catalogViewModel: CatalogViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            return catalogViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}