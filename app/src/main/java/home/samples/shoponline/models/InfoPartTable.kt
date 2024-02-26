package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "info_part_table",
    primaryKeys = ["id", "title"]
)
data class InfoPartTable(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "value")
    val value: String
)