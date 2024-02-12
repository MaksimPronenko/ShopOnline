package home.samples.shoponline.ui.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.Product
import home.samples.shoponline.models.ShopData
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.utils.ProductTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CatalogVM"

class CatalogViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    private var loadingDataResult: ShopData? = null
    private var filteredAndSortedProducts: List<Product> = emptyList()
    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())
    val productsFlow = _productsFlow.asStateFlow()
    var chosenProductType: Int = 0
    private var chosenSortingType: Int = 0

    init {
        loadCatalogData()
    }

    fun filterProducts(productType: Int) {
        chosenProductType = productType
        loadCatalogData()
    }

    fun sortProducts(sortingType: Int) {
        chosenSortingType = sortingType
        loadCatalogData()
    }

    private fun loadCatalogData() {
        Log.d(TAG, "Функция loadCatalogData() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")

            loadingDataResult = repository.getShopData()
            Log.d(TAG, loadingDataResult.toString())

            if (loadingDataResult != null) {
                filteredAndSortedProducts = filterProducts()
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
                _productsFlow.value = filteredAndSortedProducts
            } else {
                _state.value = ViewModelState.Error
                Log.d(TAG, "ViewModelState.Error")
            }
        }
    }

    private fun filterProducts(): List<Product> {
        // Перед вызовом функции проверено, что loadingDataResult != null
        val filteredByProductType: List<Product> = when (chosenProductType) {
            1 -> loadingDataResult!!.items.filter { it.tags.contains(ProductTypes.FACE.title) }
            2 -> loadingDataResult!!.items.filter { it.tags.contains(ProductTypes.BODY.title) }
            3 -> loadingDataResult!!.items.filter { it.tags.contains(ProductTypes.SUNTAN.title) }
            4 -> loadingDataResult!!.items.filter { it.tags.contains(ProductTypes.MASK.title) }
            else -> loadingDataResult!!.items
        }
        val sortedBySortingType : List<Product> = when (chosenSortingType) {
            1 -> filteredByProductType.sortedByDescending { it.price.priceWithDiscount.toInt() }
            2 -> filteredByProductType.sortedBy { it.price.priceWithDiscount.toInt() }
            else -> filteredByProductType.sortedByDescending { it.feedback.rating }
        }
        return sortedBySortingType
    }
}