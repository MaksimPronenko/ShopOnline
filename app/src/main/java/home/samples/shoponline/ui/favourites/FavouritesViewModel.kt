package home.samples.shoponline.ui.favourites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.ProductTableWithFavourites
import home.samples.shoponline.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "FavouritesVM"

class FavouritesViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    private val _favouriteProductsFlow =
        MutableStateFlow<List<ProductTableWithFavourites>>(emptyList())
    val favouriteProductsFlow = _favouriteProductsFlow.asStateFlow()
    private val _favouriteProductsChannel = Channel<List<ProductTableWithFavourites>>()
    val favouriteProductsChannel = _favouriteProductsChannel.receiveAsFlow()

    var chosenChip: Int = 0

    private var favouriteProductsList: List<ProductTableWithFavourites> = emptyList()

    fun handleChipChoice(type: Int) {
        chosenChip = type
        loadFavouritesData()
    }

    fun loadFavouritesData() {
        Log.d(TAG, "Функция loadFavouritesData() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")

            favouriteProductsList = repository.getFavouriteProductsList()

            if (favouriteProductsList.isNotEmpty()) {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
                Log.d(TAG, "favouriteProductsList = $favouriteProductsList")
                if (chosenChip == 0) _favouriteProductsFlow.value = favouriteProductsList
                else _favouriteProductsFlow.value = emptyList()
            } else {
                _state.value = ViewModelState.Error
                Log.d(TAG, "ViewModelState.Error")
            }
        }
    }

    fun removeFavourite(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addOrRemoveFavourite(id, false)
            favouriteProductsList = favouriteProductsList.filter{ it.productTable.productDataTable.id != id }
            _favouriteProductsChannel.send(favouriteProductsList)
        }
    }
}