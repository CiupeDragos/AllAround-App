package com.example.allaroundapp.ui.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.other.Resource
import com.example.allaroundapp.repositories.AbstractRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AbstractRepository
): ViewModel() {

    sealed class LoginEvent {
        object FieldEmptyError: LoginEvent()

        data class LoginSuccess(val data: String): LoginEvent()
        data class LoginError(val error: String): LoginEvent()
    }

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent

    fun validateCredentialsAndLogin(username: String, password: String) {
        viewModelScope.launch {
            if(username.trim().isEmpty() || password.isEmpty()) {
                _loginEvent.emit(LoginEvent.FieldEmptyError)
                return@launch
            }
            when(val result = repository.loginAccount(username, password)) {
                is Resource.Success -> {
                    _loginEvent.emit(LoginEvent.LoginSuccess(result.data!!))
                }
                is Resource.Error -> {
                    _loginEvent.emit(LoginEvent.LoginError(result.message!!))
                }
            }
        }
    }
}