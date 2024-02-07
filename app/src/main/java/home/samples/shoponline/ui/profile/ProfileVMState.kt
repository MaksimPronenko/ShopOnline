package home.samples.shoponline.ui.profile

@Suppress("ConvertObjectToDataObject")
sealed class ProfileVMState {
    object Loading : ProfileVMState()
    object Loaded : ProfileVMState()
    object Error : ProfileVMState()
}