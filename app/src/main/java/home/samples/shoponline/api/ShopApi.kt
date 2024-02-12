package home.samples.shoponline.api

import home.samples.shoponline.models.ShopData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ShopApi {

    @GET("97e721a7-0a66-4cae-b445-83cc0bcf9010")
    suspend fun getShopData(): ShopData?

}

val retrofit: ShopApi = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl("https://run.mocky.io/v3/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ShopApi::class.java)