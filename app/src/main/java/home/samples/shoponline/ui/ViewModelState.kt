package home.samples.shoponline.ui

@Suppress("ConvertObjectToDataObject")
sealed class ViewModelState {
    object Loading : ViewModelState()
    object Loaded : ViewModelState()
    object Error : ViewModelState()
}