package home.samples.shoponline.data

import android.util.Log
import home.samples.shoponline.api.retrofit
import home.samples.shoponline.models.Product
import home.samples.shoponline.models.ShopData
import home.samples.shoponline.models.UserTable
import javax.inject.Inject

private const val TAG = "ShopRepository"

class Repository @Inject constructor(private val dao: ShopDao) {

    suspend fun getShopData(): ShopData? {
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

    suspend fun isUserExistsInUserTable(phoneNumber: String): Boolean =
        dao.isUserExistsInUserTable(phoneNumber)

    suspend fun addUserTable(userTable: UserTable) {
        dao.addUserTable(userTable)
    }
}