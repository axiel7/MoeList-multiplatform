package ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

abstract class BaseViewModel : ScreenModel {
    var showMessage by mutableStateOf(false)
    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    protected fun setErrorMessage(message: String) {
        showMessage = true
        this.message = message
    }

    companion object {
        const val GENERIC_ERROR = "Generic Error"
    }
}