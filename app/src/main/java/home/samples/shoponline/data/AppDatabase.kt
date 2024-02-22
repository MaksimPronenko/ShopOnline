package home.samples.shoponline.data

import androidx.room.Database
import androidx.room.RoomDatabase
import home.samples.shoponline.models.FavouritesTable
import home.samples.shoponline.models.ImageTable
import home.samples.shoponline.models.InfoPartTable
import home.samples.shoponline.models.ProductDataTable
import home.samples.shoponline.models.TagTable
import home.samples.shoponline.models.UserTable

@Database(
    entities = [
        UserTable::class,
        ProductDataTable::class,
        TagTable::class,
        InfoPartTable::class,
        ImageTable::class,
        FavouritesTable::class
    ], version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}