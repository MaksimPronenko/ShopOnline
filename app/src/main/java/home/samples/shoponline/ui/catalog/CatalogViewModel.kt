package home.samples.shoponline.ui.catalog

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.App
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.ImageTable
import home.samples.shoponline.models.InfoPartTable
import home.samples.shoponline.models.Product
import home.samples.shoponline.models.ProductDataTable
import home.samples.shoponline.models.ProductTable
import home.samples.shoponline.models.ShopData
import home.samples.shoponline.models.TagTable
import home.samples.shoponline.ui.ViewModelState
import home.samples.shoponline.utils.ProductTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "CatalogVM"

class CatalogViewModel(
    private val repository: Repository,
    application: App
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    private var imagesURIStrigs: List<String> = emptyList()
    private val imagesMap: Map<String, List<Int>> = mapOf(
        "cbf0c984-7c6c-4ada-82da-e29dc698bb50" to listOf(0, 1),
        "54a876a5-2205-48ba-9498-cfecff4baa6e" to listOf(2, 3),
        "75c84407-52e1-4cce-a73a-ff2d3ac031b3" to listOf(1, 0),
        "16f88865-ae74-4b7c-9d85-b68334bb97db" to listOf(4, 5),
        "26f88856-ae74-4b7c-9d85-b68334bb97db" to listOf(3, 4),
        "15f88865-ae74-4b7c-9d81-b78334bb97db" to listOf(0, 2),
        "88f88865-ae74-4b7c-9d81-b78334bb97db" to listOf(5, 4),
        "55f58865-ae74-4b7c-9d81-b78334bb97db" to listOf(2, 1)
    )
    private var loadingDataResult: ShopData? = null
    private var filteredAndSortedProducts: List<Product> = emptyList()
    private var filteredAndSortedProductTables: MutableList<ProductTable> = mutableListOf()
    private val _productsFlow = MutableStateFlow<List<ProductTable>>(emptyList())
    val productsFlow = _productsFlow.asStateFlow()
    var chosenProductType: Int = 0
    private var chosenSortingType: Int = 0

    init {
        imagesURIStrigs = createImagesURIStrigsList(application.applicationContext)
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
                filteredAndSortedProducts.forEach {
                    filteredAndSortedProductTables.add(
                        convertProductToProductTable(it)
                    )
                }
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
                _productsFlow.value = filteredAndSortedProductTables.toList()
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

    private fun createImagesURIStrigsList(context: Context): List<String> {
        val imagesURIStrigs: MutableList<String> = mutableListOf()
        for(i in 0..5) {
            val uri = Uri.parse("android.resource://" + context.packageName + "/drawable/product_image_$i")
            imagesURIStrigs.add(i, uri.toString())
        }
        return imagesURIStrigs.toList()
    }

    private fun convertProductToProductTable(product: Product): ProductTable {
        val tags: MutableList<TagTable> = mutableListOf()
        product.tags.forEach {
            tags.add(
                TagTable(
                    id = product.id,
                    tag = it
                )
            )
        }
        val info: MutableList<InfoPartTable> = mutableListOf()
        product.info.forEach {
            info.add(
                InfoPartTable(
                    id = product.id,
                    title = it.title,
                    value = it.value
                )
            )
        }
        val images: MutableList<ImageTable> = mutableListOf()
        var imageNumbers: List<Int> = emptyList()
        imagesMap.forEach { (id, listOfImageNumbers) ->
            if (product.id == id) imageNumbers = listOfImageNumbers
        }
        imageNumbers.forEach {
            images.add(
                ImageTable(
                    id = product.id,
                    imageURIString = imagesURIStrigs[it]
                )
            )
        }

        return ProductTable(
            productDataTable = ProductDataTable(
                id = product.id,
                title = product.title,
                subtitle = product.subtitle,
                price = product.price.price,
                discount = product.price.discount,
                priceWithDiscount = product.price.priceWithDiscount,
                unit = product.price.unit,
                feedbackCount = product.feedback.count,
                rating = product.feedback.rating,
                available = product.available,
                description = product.description
            ),
            tags = tags.toList(),
            info = info.toList(),
            images = images.toList()
        )
    }
}