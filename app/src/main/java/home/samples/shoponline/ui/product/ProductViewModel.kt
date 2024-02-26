package home.samples.shoponline.ui.product

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

private const val TAG = "ProductVM"

class ProductViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    var id: String = ""

    var loadingDataResult: ProductTableWithFavourites? = null

    private var isDiscriptionTextVisible = true
    private var isIngredientsTextVisible = true

    private val _favouriteChannel = Channel<Boolean>()
    val favouriteChannel = _favouriteChannel.receiveAsFlow()

    private val _showHideDescriptionChannel = Channel<Boolean>()
    val showHideDescriptionChannel = _showHideDescriptionChannel.receiveAsFlow()

    private val _showHideIngredientsChannel = Channel<Boolean>()
    val showHideIngredientsChannel = _showHideIngredientsChannel.receiveAsFlow()

    var ingredientsTextLinesCount = 0

    fun loadProductData(receivedId: String) {
        id = receivedId
        Log.d(TAG, "Функция loadProductData() запущена. receivedId = $receivedId")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")
            loadingDataResult = repository.getProductTable(id)
            Log.d(TAG, "loadingDataResult = ${loadingDataResult.toString()}")
            if (loadingDataResult != null) {
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
            } else {
                _state.value = ViewModelState.Error
                Log.d(TAG, "ViewModelState.Error")
            }
        }
    }

    fun changeFavouriteStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentFavouriteStatus = repository.getFavourites().contains(loadingDataResult!!.productTable.productDataTable.id)
            repository.addOrRemoveFavourite(id, !currentFavouriteStatus)
            _favouriteChannel.send(element = !currentFavouriteStatus)
        }
    }

    fun showHideDescription() {
        Log.d(TAG, "showHideDescription()")
        viewModelScope.launch(Dispatchers.IO) {
            isDiscriptionTextVisible = !isDiscriptionTextVisible
            _showHideDescriptionChannel.send(isDiscriptionTextVisible)
        }
    }

    fun showHideIngredients() {
        Log.d(TAG, "showHideIngredients()")
        viewModelScope.launch(Dispatchers.IO) {
            isIngredientsTextVisible = !isIngredientsTextVisible
            _showHideIngredientsChannel.send(isIngredientsTextVisible)
        }
    }
}