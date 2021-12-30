package com.example.allaroundapp.ui.fragments.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.other.Constants.MAX_PASSWORD_LENGTH
import com.example.allaroundapp.other.Constants.MAX_USERNAME_LENGTH
import com.example.allaroundapp.other.Constants.MIN_PASSWORD_LENGTH
import com.example.allaroundapp.other.Constants.MIN_USERNAME_LENGTH
import com.example.allaroundapp.other.Resource
import com.example.allaroundapp.repositories.AbstractRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AbstractRepository
): ViewModel() {

    sealed class RegisterError {
        object EmptyFieldError: RegisterError()
        object UsernameTooShortError: RegisterError()
        object UsernameTooLongError: RegisterError()
        object PasswordTooShortError: RegisterError()
        object PasswordTooLongError: RegisterError()
        object PasswordsNotTheSameError: RegisterError()

        data class RegisterAccountError(val error: String): RegisterError()
        data class RegisterAccountSuccess(val message: String): RegisterError()
    }

    private val _registerEvent = MutableSharedFlow<RegisterError>()
    val registerEvent: SharedFlow<RegisterError> = _registerEvent

    fun validateCredentialsAndCreateAccount(username: String, password: String, confirmedPassword: String) {
        viewModelScope.launch {

            if(username.trim().isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
                _registerEvent.emit(RegisterError.EmptyFieldError)
                return@launch
            }
            if(password != confirmedPassword) {
                _registerEvent.emit(RegisterError.PasswordsNotTheSameError)
                return@launch
            }
            if(username.length < MIN_USERNAME_LENGTH) {
                _registerEvent.emit(RegisterError.UsernameTooShortError)
                return@launch
            }
            if(username.length > MAX_USERNAME_LENGTH) {
                _registerEvent.emit(RegisterError.UsernameTooLongError)
                return@launch
            }
            if(password.length < MIN_PASSWORD_LENGTH) {
                _registerEvent.emit(RegisterError.PasswordTooShortError)
                return@launch
            }
            if(username.length > MAX_PASSWORD_LENGTH) {
                _registerEvent.emit(RegisterError.PasswordTooLongError)
                return@launch
            }
            when(val result = repository.registerAccount(username, password)) {
                is Resource.Success -> {
                    _registerEvent.emit(RegisterError.RegisterAccountSuccess(result.data!!))
                }
                is Resource.Error -> {
                    _registerEvent.emit(RegisterError.RegisterAccountError(result.message!!))
                }
            }
        }
    }
}