package com.simon.vpdassesment.features.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simon.vpdassesment.core.network.NetworkResource
import com.simon.vpdassesment.data.datasource.params.CreateAccountParams
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
class CreateAccountViewmodel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    private val _createAccountState = MutableStateFlow<CreateAccountState>(CreateAccountState())
    val createAccountState: StateFlow<CreateAccountState> = _createAccountState


    fun register(onError: (String) -> Unit, onSuccess: () -> Unit) = viewModelScope.launch {
        val state = createAccountState.value
        authRepositoryImpl.createAccount(CreateAccountParams(state.email, state.password,state.name))
            .collectLatest {
                val loading = it is NetworkResource.Loading
                val serverError = it is NetworkResource.Error
                val errorMessage = if (serverError) (it as NetworkResource.Error).message else null
                _createAccountState.update {
                    it.copy(
                        loading = loading,
                        servererror = errorMessage
                    )
                }
                if (it is NetworkResource.Success) {
                    _createAccountState.value = CreateAccountState()
                    onSuccess()
                }
                if (it is NetworkResource.Error) {
                    onError(it.message)
                }

            }
    }

    fun updateEmail(email: String) {
        _createAccountState.update { currentState ->
            currentState.copy(email = email, error = null)
        }
        validateInput()
    }

    fun updateName(name: String) {
        _createAccountState.update { currentState ->
            currentState.copy(name = name, errorname = null)
        }
        validateInput()
    }

    fun updatePassword(password: String) {
        _createAccountState.update { currentState ->
            currentState.copy(password = password, errorPassword = null)
        }
        validateInput()
    }

    fun updateVerifyPassword(verifyPassword: String) {
        _createAccountState.update { currentState ->
            currentState.copy(verifypassword = verifyPassword, errorverifyPassword = null)
        }
        validateInput()
    }

    private fun validateInput() {
        val currentState = _createAccountState.value
        val emailError = when {
            currentState.email.isBlank() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email)
                .matches() -> "Invalid email format"

            else -> null
        }

        val nameError = if (currentState.name.isBlank()) "Name cannot be empty" else null

        val passwordError = when {
            currentState.password.isBlank() -> "Password cannot be empty"
            currentState.password.length < 8 -> "Password must be at least 8 characters"
            !currentState.password.any { it.isDigit() } -> "Password must contain at least one digit"
            !currentState.password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            else -> null
        }

        val verifyPasswordError = when {
            currentState.verifypassword.isBlank() -> "Please confirm your password"
            currentState.verifypassword != currentState.password -> "Passwords do not match"
            else -> null
        }

        _createAccountState.update {
            it.copy(
                error = emailError,
                errorname = nameError,
                errorPassword = passwordError,
                errorverifyPassword = verifyPasswordError
            )
        }
    }


}