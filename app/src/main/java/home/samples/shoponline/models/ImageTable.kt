package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "image_table",
    primaryKeys = ["id", "uri"]
)
data class ImageTable(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "uri")
    val imageURIString: String
)