package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserTable(
    @PrimaryKey
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "surname")
    val surname: String
)