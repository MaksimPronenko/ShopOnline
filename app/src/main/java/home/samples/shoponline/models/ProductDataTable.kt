package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_data_table")
data class ProductDataTable(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "subtitle")
    val subtitle: String,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "discount")
    var discount: Int,
    @ColumnInfo(name = "price_with_discount")
    val priceWithDiscount: String,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "feedback_count")
    var feedbackCount: Int,
    @ColumnInfo(name = "rating")
    val rating: Float,
    @ColumnInfo(name = "available")
    var available: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "info_part_title")
    val infoPartTitle: String,
    @ColumnInfo(name = "info_part_value")
    val infoPartValue: String
)
