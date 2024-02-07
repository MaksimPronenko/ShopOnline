package home.samples.shoponline.ui.main

@Suppress("ConvertObjectToDataObject")
sealed class MainVMState {
    object Loading : MainVMState()
    object Loaded : MainVMState()
    object Error : MainVMState()
}