package home.samples.shoponline

import android.app.Application
import androidx.room.Room
import dagger.hilt.android.HiltAndroidApp
import home.samples.shoponline.data.AppDatabase
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor() : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}