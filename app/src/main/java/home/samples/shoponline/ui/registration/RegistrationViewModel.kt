package home.samples.shoponline.ui.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val TAG = "RegistrationVM"

class RegistrationViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<RegistrationVMState>(
        RegistrationVMState.WorkingState(
            firstNameState = true,
            surnameState = true,
            phoneState = true
        )
    )
    val state = _state.asStateFlow()

    private var firstName: String = ""
    private var firstNameState: Boolean? = null

    private var surname: String = ""
    var surnameState: Boolean? = null

    private var phoneNumber: String = ""
    var phoneNumberState: Boolean? = null
    var receivedDigits: String = "" // Полученные цифры номера без учёта +7

    private val _registrationResult = Channel<Boolean>()
    val registrationResult = _registrationResult.receiveAsFlow()

    fun register() {
        // Эта функция может быть запущена, только если значения во всех полях ввода прошли валидацию.
        Log.d(TAG, "Функция register() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = RegistrationVMState.RegistrationProcess
            Log.d(TAG, "RegistrationVMState.RegistrationProcess")

            val isAlreadyRegistered = repository.isUserExistsInUserTable(phoneNumber)
            Log.d(TAG, "Пользователь уже есть в базе: $firstName $surname, $phoneNumber.")

            if (!isAlreadyRegistered) {
                repository.addUserTable(
                    UserTable(
                        phoneNumber = phoneNumber,
                        firstName = firstName,
                        surname = surname
                    )
                )
                Log.d(TAG, "Пользователь добавлен в базу: $firstName $surname, $phoneNumber.")
            }

            _state.value =
                RegistrationVMState.WorkingState(firstNameState, surnameState, phoneNumberState)
            Log.d(TAG, "RegistrationVMState.WorkingState")
            _registrationResult.send(isAlreadyRegistered)
        }
    }

    fun handleEnteredFirstName(enteredText: String) {
        firstName = enteredText
        Log.d(TAG, "handleEnteredFirstName($firstName)")
        firstNameState = isCyrillicName(firstName) && firstName.isNotEmpty()
        Log.d(TAG, "firstNameState = $firstNameState")
        refreshWorkingState()
    }

    fun handleEnteredSurname(enteredText: String) {
        surname = enteredText
        Log.d(TAG, "handleEnteredFirstName($surname)")
        surnameState = isCyrillicName(surname) && surname.isNotEmpty()
        Log.d(TAG, "surnameState = $surnameState")
        refreshWorkingState()
    }

    fun handleEnteredPhoneNumber() {
        phoneNumberState = receivedDigits.length == 10
        Log.d(TAG, "handleEnteredPhoneNumber(): phoneNumberState = $phoneNumberState")
        refreshWorkingState()
    }

    private fun isCyrillicName(testString: String): Boolean {
        val regexCyrillic = "[А-яЁё]+"
        val p = Pattern.compile(regexCyrillic)
        val m: Matcher = p.matcher(testString)
        return m.matches()
    }

    private fun refreshWorkingState() {
        viewModelScope.launch {
            _state.value =
                RegistrationVMState.WorkingState(firstNameState, surnameState, phoneNumberState)
        }
    }
}