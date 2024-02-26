package home.samples.shoponline.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.models.UserTable
import home.samples.shoponline.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProfileVM"

class ProfileViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    private var currentUserData: UserTable? = null
    var nameAndSurname: String = ""
    var phoneNumber: String = ""
    var favouriteProductCount = 0

    fun loadProfileData() {
        Log.d(TAG, "Функция login() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
            Log.d(TAG, "ViewModelState.Loading")
            val currentUserPhoneNumber = repository.getCurrenUserPhoneNumber()
            currentUserData = repository.getUserTable(currentUserPhoneNumber)
            favouriteProductCount = repository.getFavouritesCount()

            if (currentUserData != null) {
                nameAndSurname = "${currentUserData!!.firstName} ${currentUserData!!.surname}"
                phoneNumber = formatPhoneNumber(savedPhoneNumber = currentUserData!!.phoneNumber)
                _state.value = ViewModelState.Loaded
                Log.d(TAG, "ViewModelState.Loaded")
            } else {
                _state.value = ViewModelState.Error
                Log.d(TAG, "ViewModelState.Error")
            }
        }
    }

    private fun formatPhoneNumber(savedPhoneNumber: String): String {
        var formattedPhoneNumber = ""
        savedPhoneNumber.forEachIndexed { index, char ->
            when(index) {
                2, 5 -> formattedPhoneNumber += " $char"
                8, 10 -> formattedPhoneNumber += "-$char"
                else -> formattedPhoneNumber += char
            }
        }
        return formattedPhoneNumber
    }

    fun clearDataOnExit() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCatalogData()
            repository.clearCurrentUserTable()
        }
    }
}