package home.samples.shoponline.ui.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import home.samples.shoponline.data.Repository
import home.samples.shoponline.ui.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ProductVM"

class ProductViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _state = MutableStateFlow<ViewModelState>(ViewModelState.Loading)
    val state = _state.asStateFlow()

    fun loadProductData() {
        Log.d(TAG, "Функция login() запущена")
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewModelState.Loading
//            Log.d(TAG, "ViewModelState.Loading")
//
//            val loadingDataResult = repository.getShopData()
//            Log.d(TAG, loadingDataResult.toString())
//
//            if (loadingDataResult != null) {
                _state.value = ViewModelState.Loaded
//                Log.d(TAG, "ViewModelState.Loaded")
//            } else {
//                _state.value = ViewModelState.Error
//                Log.d(TAG, "ViewModelState.Error")
//            }
        }
    }
}