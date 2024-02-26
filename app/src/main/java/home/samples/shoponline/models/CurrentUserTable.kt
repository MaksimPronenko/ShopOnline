package home.samples.shoponline.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user_table")
data class CurrentUserTable(
    @PrimaryKey
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String
)