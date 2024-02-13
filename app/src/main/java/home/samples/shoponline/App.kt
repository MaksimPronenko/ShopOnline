package home.samples.shoponline

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.room.Room
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import home.samples.shoponline.data.AppDatabase
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