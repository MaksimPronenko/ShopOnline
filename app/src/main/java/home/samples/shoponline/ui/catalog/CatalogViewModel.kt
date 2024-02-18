package home.samples.shoponline.ui.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.ProductTableWithFavourites
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.utils.ProductTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "CatalogVM"

class CatalogViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    private var loadingDataResult: List<ProductTableWithFavourites>? = null
    private var filteredAndSortedProducts: List<ProductTableWithFavourites> = emptyList()
    private val _productsFlow = MutableStateFlow<List<ProductTableWithFavourites>>(emptyList())
    val productsFlow = _productsFlow.asStateFlow()
    private val _productsChannel = Channel<List<ProductTableWithFavourites>>()
    val productsChannel = _productsChannel.receiveAsFlow()
    var chosenProductType: Int = 0
    private var chosenSortingType: Int = 0

    private var catalogFirstOpened = true
    /* При первом открытии каталога (за время работы приложения) в репозитории будет обнулена
    таблица товаров, но отдельная таблица с id избранных товаров останется. Это нужно для обновления
    списка товаров цен при каждом запуске приложения, после чего каталог начинает загружать
    данные из базы данных. */

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

            loadingDataResult = repository.getCatalogData(catalogFirstOpened)
            Log.d(TAG, loadingDataResult.toString())

            if (loadingDataResult != null) {
                catalogFirstOpened = false
                Log.d(TAG, "loadingDataResult.items.size = ${loadingDataResult!!.size}")
                filteredAndSortedProducts = filterAndSortProducts()
                Log.d(TAG, "Products amount = ${filteredAndSortedProducts.size}")
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
                _productsFlow.value = filteredAndSortedProducts
            } else {
                _state.value = ViewModelState.Error
                Log.d(TAG, "ViewModelState.Error")
            }
        }
    }

    private fun filterAndSortProducts(): List<ProductTableWithFavourites> {
        // Перед вызовом функции проверено, что loadingDataResult != null
        val filteredByProductType: List<ProductTableWithFavourites> = when (chosenProductType) {
            1 -> loadingDataResult!!.filter { checkTags(it, ProductTypes.FACE.title) }
            2 -> loadingDataResult!!.filter { checkTags(it, ProductTypes.BODY.title) }
            3 -> loadingDataResult!!.filter { checkTags(it, ProductTypes.SUNTAN.title) }
            4 -> loadingDataResult!!.filter { checkTags(it, ProductTypes.MASK.title) }
            else -> loadingDataResult!!
        }
        val sortedBySortingType: List<ProductTableWithFavourites> = when (chosenSortingType) {
            1 -> filteredByProductType.sortedByDescending { it.productTable.productDataTable.priceWithDiscount.toInt() }
            2 -> filteredByProductType.sortedBy { it.productTable.productDataTable.priceWithDiscount.toInt() }
            else -> filteredByProductType.sortedByDescending { it.productTable.productDataTable.rating }
        }
        return sortedBySortingType
    }

    private fun checkTags(
        productTableWithFavourites: ProductTableWithFavourites,
        searchedType: String
    ): Boolean {
        var isFound = false
        productTableWithFavourites.productTable.tags.forEach { tagTable ->
            if (tagTable.tag == searchedType) isFound = true
        }
        return isFound
    }

    fun addOrRemoveFavourite(id: String, operation: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addOrRemoveFavourite(id, operation)
            val refreshedProductList: MutableList<ProductTableWithFavourites> = mutableListOf()
            filteredAndSortedProducts.forEach {
                if (it.productTable.productDataTable.id == id) {
                    refreshedProductList.add(
                        ProductTableWithFavourites(
                            productTable = it.productTable,
                            favourite = operation
                        )
                    )
                } else {
                    refreshedProductList.add(it)
                }
            }
            filteredAndSortedProducts = refreshedProductList
            _productsChannel.send(refreshedProductList.toList())
        }
    }
}