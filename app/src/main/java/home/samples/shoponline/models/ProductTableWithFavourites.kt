package home.samples.shoponline.models

data class ProductTableWithFavourites(
    val productTable: ProductTable,
    val favourite: Boolean
)