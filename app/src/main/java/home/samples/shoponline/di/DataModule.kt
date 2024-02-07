package home.samples.shoponline.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import home.samples.shoponline.data.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

//    @Provides
//    @Singleton
//    fun provideApp(@ApplicationContext context: Context): App {
//        return context as App
//    }

    @Provides
    @Singleton
    fun provideRepository(): Repository {
        return Repository()
    }
}