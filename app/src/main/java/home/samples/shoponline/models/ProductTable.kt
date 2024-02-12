package home.samples.shoponline.models

class ProductTable(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: String,
    var discount: Int,
    val priceWithDiscount: String,
    val unit: String,
    var feedbackCount: Int,
    val rating: Float,
    val tags: List<String>,
    var available: Int,
    val description: String,
    val infoPartTitle: String,
    val infoPartValue: String,
    val uriList: List<String>
)