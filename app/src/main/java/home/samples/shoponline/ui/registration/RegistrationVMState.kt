package home.samples.shoponline.ui.registration

@Suppress("ConvertObjectToDataObject")
sealed class RegistrationVMState {
    object LoginProcess : RegistrationVMState()
    data class WorkingState(val firstNameState: Boolean?, val surnameState: Boolean?, val phoneState: Boolean?) : RegistrationVMState()
}