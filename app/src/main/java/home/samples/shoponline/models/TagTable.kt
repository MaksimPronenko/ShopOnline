package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "tag_table",
    primaryKeys = ["id", "tag"]
)
data class TagTable(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "tag")
    val tag: String
)