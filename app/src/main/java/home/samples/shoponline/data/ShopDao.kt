package home.samples.shoponline.data

import androidx.room.*
import home.samples.shoponline.models.CurrentUserTable
import home.samples.shoponline.models.FavouritesTable
import home.samples.shoponline.models.ImageTable
import home.samples.shoponline.models.InfoPartTable
import home.samples.shoponline.models.ProductDataTable
import home.samples.shoponline.models.ProductTable
import home.samples.shoponline.models.TagTable
import home.samples.shoponline.models.UserTable

@Dao
interface ShopDao {

    // Проверка наличия пользователя в базе данных
    @Query("SELECT EXISTS (SELECT * FROM user_table WHERE phone_number = :phoneNumber)")
    suspend fun isUserExistsInUserTable(phoneNumber: String): Boolean

    // Запрос на добавление нового пользователя
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserTable(userTable: UserTable)

    // Запрос на получение данных пользователя по телефонному номеру
    @Query("SELECT * FROM user_table WHERE phone_number LIKE :phoneNumber")
    suspend fun getUserTable(phoneNumber: String): UserTable?

    // Запрос на добавление текущего пользователя
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addCurrentUserTable(currentUserTable: CurrentUserTable)

    // Запрос на удаление текущего пользователя
    @Query("DELETE FROM current_user_table")
    suspend fun removeCurrentUserTable()

    // Запрос текущего пользователя. Это будет List размера 1.
    @Query("SELECT * FROM current_user_table")
    suspend fun getCurrentUserTableList(): List<CurrentUserTable>

    // Запрос на добавление новой записи данных товара
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductDataTable(productDataTable: ProductDataTable)

    // Запрос на добавление новой записи тегов товара
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTagTable(tagTable: TagTable)

    // Запрос на добавление новой записи информации о товаре
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInfoPartTable(infoPartTable: InfoPartTable)

    // Запрос на добавление новой записи изображений товара
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageTable(imageTable: ImageTable)

    // Запрос на получение данных товара ProductTable по id
    @Query("SELECT * FROM product_data_table WHERE id LIKE :id")
    suspend fun getProductTable(id: String): ProductTable?

    // Запрос на получение списка товаров
    @Query("SELECT * FROM product_data_table")
    suspend fun getProductTableList(): List<ProductTable>

    // Запрос на очистку таблицы товаров
    @Query("DELETE FROM product_data_table")
    suspend fun removeProductDataTable()

    // Запрос на очистку таблицы тегов
    @Query("DELETE FROM tag_table")
    suspend fun removeTagTable()

    // Запрос на очистку таблицы тегов
    @Query("DELETE FROM info_part_table")
    suspend fun removeInfoPartTable()

    // Запрос на очистку таблицы изображений
    @Query("DELETE FROM image_table")
    suspend fun removeImageTable()

    // Запрос на получение списка избранных товаров
    @Query("SELECT * FROM favourites_table")
    suspend fun getFavouritesTableList(): List<FavouritesTable>

    // Запрос на добавление товара в избранное
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouritesTable(favouritesTable: FavouritesTable)

    // Запрос на удаление товара из таблицы избранных
    @Query("DELETE FROM favourites_table WHERE id = :id")
    suspend fun removeFavouritesTable(id: String)

    // Запрос на очистку всей таблицы избранных
    @Query("DELETE FROM favourites_table")
    suspend fun removeAllFavourite()

    // Запрос на проверку, является ли товар избранным
    @Query("SELECT EXISTS (SELECT id FROM favourites_table WHERE id LIKE :id)")
    suspend fun isProductFavourite(id: String): Boolean

    // Запрос на получение количества записей в таблице избранных товаров
    @Query("SELECT COUNT(*) FROM favourites_table")
    suspend fun getFavouritesCount(): Int
}