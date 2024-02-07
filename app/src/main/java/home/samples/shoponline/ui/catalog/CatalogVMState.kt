package home.samples.shoponline.ui.catalog

@Suppress("ConvertObjectToDataObject")
sealed class CatalogVMState {
    object Loading : CatalogVMState()
    object Loaded : CatalogVMState()
    object Error : CatalogVMState()
}