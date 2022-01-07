package com.example.allaroundapp.ui.fragments.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.data.responses.FriendRequestsResponse
import com.example.allaroundapp.other.Resource
import com.example.allaroundapp.repositories.AbstractRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: AbstractRepository
): ViewModel() {

    sealed class ActionEvents {
        data class RequestEvent(val data: String): ActionEvents()

        data class SearchEvent(val data: List<String>): ActionEvents()
    }

    private val _apiEvents = MutableStateFlow(listOf<String>())
    val apiEvents: StateFlow<List<String>> = _apiEvents


    private val _actionEvents = MutableSharedFlow<ActionEvents>()
    val actionEvents: SharedFlow<ActionEvents> = _actionEvents


    fun getFriends() {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.findFriends()) {
                is Resource.Success -> {
                    _apiEvents.value = result.data!!
                }
                is Resource.Error -> {
                    _apiEvents.value = listOf()
                }
            }
        }
    }

    fun getReceivedRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            val networkCall = getFriendRequests()
            if(networkCall.receivedRequests.isNotEmpty()) {
                _apiEvents.value = networkCall.receivedRequests
            }
        }
    }

    fun getSentRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            val networkCall = getFriendRequests()
            if(networkCall.sentRequests.isNotEmpty()) {
                _apiEvents.value = networkCall.sentRequests
            }
        }
    }

    private suspend fun getFriendRequests(): FriendRequestsResponse {
        return when(val result = repository.getFriendRequests()) {
            is Resource.Success -> {
                result.data!!
            }
            is Resource.Error -> {
                FriendRequestsResponse(listOf(), listOf())
            }
        }
    }

    fun cancelFriendRequest(sentToUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.cancelFriendRequest(sentToUsername)) {
                is Resource.Success -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.data!!))
                    _apiEvents.value -= sentToUsername
                }
                is Resource.Error -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.message!!))
                }
            }
        }
    }

    fun denyFriendRequest(senderUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.refuseFriendRequest(senderUsername)) {
                is Resource.Success -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.data!!))
                    _apiEvents.value -= senderUsername
                }
                is Resource.Error -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.message!!))
                }
            }
        }
    }

    fun acceptFriendRequest(senderUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.acceptFriendRequest(senderUsername)) {
                is Resource.Success -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.data!!))
                    _apiEvents.value -= senderUsername
                }
                is Resource.Error -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.message!!))
                }
            }
        }
    }

    fun sendFriendRequest(sentToUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.sendFriendRequest(sentToUsername)) {
                is Resource.Success -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.data!!))
                }
                is Resource.Error -> {
                    _actionEvents.emit(ActionEvents.RequestEvent(result.message!!))
                }
            }
        }
    }

    fun findUsers(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.findUsers(username)) {
                is Resource.Success -> {
                    _actionEvents.emit(ActionEvents.SearchEvent(result.data!!))
                }
                is Resource.Error -> {
                    _actionEvents.emit(ActionEvents.SearchEvent(listOf()))
                    println("Value to users set to empty list")
                    Log.d("Search error", result.message!!)
                }
            }
        }
    }
}