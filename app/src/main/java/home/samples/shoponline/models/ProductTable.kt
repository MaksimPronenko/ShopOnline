package home.samples.shoponline.models

import androidx.room.Embedded
import androidx.room.Relation

class ProductTable(
    @Embedded
    val productDataTable: ProductDataTable,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val tags: List<TagTable>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val images: List<ImageTable>
)