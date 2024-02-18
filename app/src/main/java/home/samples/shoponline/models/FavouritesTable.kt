package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites_table")
data class FavouritesTable(
//    @PrimaryKey(autoGenerate = true)
//    val position: Int,
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String
)
//{
//    constructor(id: String) : this(0, id)
//}