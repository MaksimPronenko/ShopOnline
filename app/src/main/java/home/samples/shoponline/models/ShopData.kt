package home.samples.shoponline.models

data class ShopData(
    val items: List<Product>
)

data class Product(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: Price,
    val feedback: Feedback,
    val tags: List<String>,
    var available: Int,
    val description: String,
    val ingredients: String,
    val info: List<InfoPart>
)

data class Price(
    val price: String,
    var discount: Int,
    val priceWithDiscount: String,
    val unit: String
)

data class Feedback(
    var count: Int,
    val rating: Float
)

data class InfoPart(
    val title: String,
    val value: String
)