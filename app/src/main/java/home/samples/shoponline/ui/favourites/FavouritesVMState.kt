package home.samples.shoponline.ui.favourites

@Suppress("ConvertObjectToDataObject")
sealed class FavouritesVMState {
    object Loading : FavouritesVMState()
    object Loaded : FavouritesVMState()
    object Error : FavouritesVMState()
}