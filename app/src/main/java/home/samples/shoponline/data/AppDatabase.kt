package home.samples.shoponline.data

import androidx.room.Database
import androidx.room.RoomDatabase
import home.samples.shoponline.models.UserTable

@Database(
    entities = [
        UserTable::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}