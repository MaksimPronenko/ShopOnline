package home.samples.shoponline.data

import android.util.Log
import home.samples.shoponline.api.retrofit
import home.samples.shoponline.models.Product
import javax.inject.Inject

private const val TAG = "ShopRepository"

class Repository @Inject constructor() {

    suspend fun getShopData(): List<Product>? {
        kotlin.runCatching {
            retrofit.getShopData()
        }.fold(
            onSuccess = {
                Log.d(TAG, it.toString())
                return it
            },
            onFailure = {
                Log.d(TAG, "Failure")
                return null
            }
        )
    }

}