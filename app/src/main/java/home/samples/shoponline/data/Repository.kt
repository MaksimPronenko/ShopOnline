package home.samples.shoponline.data

import android.content.Context
import android.net.Uri
import android.util.Log
import home.samples.shoponline.App
import home.samples.shoponline.api.retrofit
import home.samples.shoponline.models.CurrentUserTable
import home.samples.shoponline.models.FavouritesTable
import home.samples.shoponline.models.ImageTable
import home.samples.shoponline.models.InfoPartTable
import home.samples.shoponline.models.Product
import home.samples.shoponline.models.ProductDataTable
import home.samples.shoponline.models.ProductTable
import home.samples.shoponline.models.ProductTableWithFavourites
import home.samples.shoponline.models.ShopData
import home.samples.shoponline.models.TagTable
import home.samples.shoponline.models.UserTable
import javax.inject.Inject

private const val TAG = "ShopRepository"

class Repository @Inject constructor(val application: App, private val dao: ShopDao) {

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

    private fun createImagesURIStrigsList(context: Context): List<String> {
        val imagesURIStrigs: MutableList<String> = mutableListOf()
        for (i in 0..5) {
            val uri =
                Uri.parse("android.resource://" + context.packageName + "/drawable/product_image_$i")
            imagesURIStrigs.add(i, uri.toString())
        }
        return imagesURIStrigs.toList()
    }

    suspend fun getCatalogData(catalogFirstOpened: Boolean): List<ProductTableWithFavourites>? {
        val shopData: ShopData? // Товары каталога из Api.
        var productTableList: List<ProductTable>  // Товары каталога из Dao.
        if (catalogFirstOpened) {
            clearCatalogData()
            shopData = loadCatalogDataFromApi()
            if (shopData != null) productTableList = convertShopDataToProductTableList(shopData)
            else return null
        } else {
            productTableList = dao.getProductTableList()
            if (productTableList.isEmpty()) {
                shopData = loadCatalogDataFromApi()
                if (shopData != null) {
                    productTableList = convertShopDataToProductTableList(shopData)
                    productTableList.forEach { productTable ->
                        dao.addProductDataTable(productTable.productDataTable)
                        productTable.tags.forEach { tagTable ->
                            dao.addTagTable(tagTable)
                        }
                        productTable.info.forEach { infoPartTable ->
                            dao.addInfoPartTable(infoPartTable)
                        }
                        productTable.images.forEach { imageTable ->
                            dao.addImageTable(imageTable)
                        }
                    }
                }
                else return null
            }
        }

        val favoritesList: List<String> = getFavourites() // Список id избранных товаров.
        val productTableWithFavourites: MutableList<ProductTableWithFavourites> =
            mutableListOf()
        productTableList.forEach {
            productTableWithFavourites.add(
                ProductTableWithFavourites(
                    productTable = it,
                    favourite = favoritesList.contains(it.productDataTable.id)
                )
            )
        }
        return productTableWithFavourites.toList()
    }

    private suspend fun loadCatalogDataFromApi(): ShopData? {
        kotlin.runCatching {
            retrofit.getShopData()
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }

    suspend fun getFavourites(): List<String> {
        val favouritesTableList: List<FavouritesTable> = dao.getFavouritesTableList()
        val favourites: MutableList<String> = mutableListOf()
        favouritesTableList.forEach {
            favourites.add(
                it.id
            )
        }
        return favourites.toList()
    }

    private suspend fun clearCatalogData() {
        dao.removeProductDataTable()
        dao.removeTagTable()
        dao.removeInfoPartTable()
        dao.removeImageTable()
    }

    private fun convertShopDataToProductTableList(shopData: ShopData): List<ProductTable> {
        val productTableMutableList: MutableList<ProductTable> = mutableListOf()
        shopData.items.forEach {
            productTableMutableList.add(
                convertProductToProductTable(it)
            )
        }
        return productTableMutableList.toList()
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
        val imagesURIStrigs = createImagesURIStrigsList(application.applicationContext)
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
                description = product.description,
                ingredients = product.ingredients
            ),
            tags = tags.toList(),
            info = info.toList(),
            images = images.toList()
        )
    }

    suspend fun isUserExistsInUserTable(phoneNumber: String): Boolean =
        dao.isUserExistsInUserTable(phoneNumber)

    suspend fun addUserTable(userTable: UserTable) {
        dao.addUserTable(userTable)
    }

    suspend fun addOrRemoveFavourite(id: String, operation: Boolean) {
        if (operation) {
            dao.addFavouritesTable(
                FavouritesTable(
                    id = id
                )
            )
        } else {
            dao.removeFavouritesTable(id = id)
        }
    }

    suspend fun getProductTable(id: String): ProductTableWithFavourites? {
        val productTable: ProductTable? = dao.getProductTable(id)
        return if(productTable != null) {
            val favourite: Boolean = dao.isProductFavourite(id)
            ProductTableWithFavourites(
                productTable = productTable,
                favourite = favourite
            )
        } else null
    }

    suspend fun saveCurrentUserTable(currentUserTable: CurrentUserTable) {
        dao.removeCurrentUserTable()
        dao.addCurrentUserTable(currentUserTable)
    }

    suspend fun getUserTable(phoneNumber: String): UserTable? {
        return dao.getUserTable(phoneNumber)
    }
}