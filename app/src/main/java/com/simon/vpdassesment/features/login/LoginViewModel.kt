package com.simon.vpdassesment.features.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.LoginParams
import com.simon.vpdassesment.data.repositories.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState())
    val loginState: StateFlow<LoginState> = _loginState


    fun updateEmail(email: String) {
        _loginState.update { currentState ->
            currentState.copy(email = email, error = null, errorPassword = null)
        }
        checkInput()
    }

    fun login(onError:(String)->Unit, onSuccess: () -> Unit) = viewModelScope.launch {
        val state = loginState.value
        authRepositoryImpl.login(LoginParams(state.email, state.password)).collectLatest {
            val loading = it is NetworkResource.Loading
            val serverError = it is NetworkResource.Error
            val errorMessage = if (serverError) (it as NetworkResource.Error).message else null
            _loginState.update {
                it.copy(
                    loading = loading,
                    servererror = errorMessage
                )
            }
            if (it is NetworkResource.Success) {
                _loginState.value = LoginState()
                onSuccess()
            }
            if (it is NetworkResource.Error) {
                onError(it.message)
            }

        }
    }

    fun updatePassword(password: String) {
        Napier.e("Here")
        _loginState.update { currentState ->
            currentState.copy(password = password, error = null, errorPassword = null)
        }
        checkInput()
    }

    private fun checkInput() {
        val currentState = _loginState.value

        val emailError = when {
            currentState.email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email)
                .matches() -> "Invalid email format"

            else -> null
        }

//        val passwordError = when {
//            currentState.password.isBlank() -> "Password cannot be empty"
////            currentState.password.length < 8 -> "Password must be at least 8 characters"
////            !currentState.password.any { it.isDigit() } -> "Password must contain at least one digit"
////            !currentState.password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
//            else -> null
//        }

        _loginState.update { it.copy(error = emailError, errorPassword = null) }
    }


}