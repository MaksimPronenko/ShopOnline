package home.samples.shoponline.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import home.samples.shoponline.App
import home.samples.shoponline.data.Repository
import home.samples.shoponline.data.ShopDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext context: Context): App {
        return context as App
    }

    @Provides
    @Singleton
    fun provideFilmDao(application: App): ShopDao {
        return application.db.shopDao()
    }

    @Provides
    @Singleton
    fun provideRepository(dao: ShopDao): Repository {
        return Repository(dao)
    }
}