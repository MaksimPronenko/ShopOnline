package home.samples.shoponline.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class FavouritesViewModelFactory (private val favouritesViewModel: FavouritesViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
            return favouritesViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}