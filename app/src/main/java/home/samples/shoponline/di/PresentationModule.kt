package home.samples.shoponline.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import home.samples.shoponline.data.Repository
import home.samples.shoponline.ui.catalog.CatalogViewModel
import home.samples.shoponline.ui.catalog.CatalogViewModelFactory
import home.samples.shoponline.ui.favourites.FavouritesViewModel
import home.samples.shoponline.ui.favourites.FavouritesViewModelFactory
import home.samples.shoponline.ui.product.ProductViewModel
import home.samples.shoponline.ui.product.ProductViewModelFactory
import home.samples.shoponline.ui.profile.ProfileViewModel
import home.samples.shoponline.ui.profile.ProfileViewModelFactory
import home.samples.shoponline.ui.registration.RegistrationViewModel
import home.samples.shoponline.ui.registration.RegistrationViewModelFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PresentationModule {
    @Provides
    fun provideRegistrationViewModel(
        repository: Repository
    ): RegistrationViewModel {
        return RegistrationViewModel(
            repository
        )
    }

    @Provides
    fun provideRegistrationViewModelFactory(registrationViewModel: RegistrationViewModel): RegistrationViewModelFactory {
        return RegistrationViewModelFactory(registrationViewModel)
    }

    @Provides
    @Singleton
    fun provideCatalogViewModel(
        repository: Repository
    ): CatalogViewModel {
        return CatalogViewModel(
            repository
        )
    }

    @Provides
    @Singleton
    fun provideCatalogViewModelFactory(catalogViewModel: CatalogViewModel): CatalogViewModelFactory {
        return CatalogViewModelFactory(catalogViewModel)
    }

    @Provides
    fun provideProductViewModel(
        repository: Repository
    ): ProductViewModel {
        return ProductViewModel(
            repository
        )
    }

    @Provides
    fun provideProductViewModelFactory(productViewModel: ProductViewModel): ProductViewModelFactory {
        return ProductViewModelFactory(productViewModel)
    }

    @Provides
    fun provideProfileViewModel(
        repository: Repository
    ): ProfileViewModel {
        return ProfileViewModel(
            repository
        )
    }

    @Provides
    fun provideProfileViewModelFactory(profileViewModel: ProfileViewModel): ProfileViewModelFactory {
        return ProfileViewModelFactory(profileViewModel)
    }

    @Provides
    fun provideFavouritesViewModel(
        repository: Repository
    ): FavouritesViewModel {
        return FavouritesViewModel(
            repository
        )
    }

    @Provides
    fun provideFavouritesViewModelFactory(favouritesViewModel: FavouritesViewModel): FavouritesViewModelFactory {
        return FavouritesViewModelFactory(favouritesViewModel)
    }
}