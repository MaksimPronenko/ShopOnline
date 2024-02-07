package home.samples.shoponline.ui.product

import home.samples.shoponline.ui.ViewModelState

@Suppress("ConvertObjectToDataObject")
sealed class ProductVMState {
    object Loading : ProductVMState()
    object Loaded : ProductVMState()
    object Error : ProductVMState()
}