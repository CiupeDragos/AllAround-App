package com.example.allaroundapp.ui.fragments.creategroup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.adapters.SelectedGroupMember
import com.example.allaroundapp.other.Resource
import com.example.allaroundapp.repositories.AbstractRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val repository: AbstractRepository
): ViewModel() {

    sealed class GroupEvents {
        object GroupNameTooShort: GroupEvents()
        object GroupNameTooLong: GroupEvents()
        object GroupNameEmpty: GroupEvents()

        data class CreateGroupError(val error: String): GroupEvents()
        data class GetFriendsApiCall(val friends: List<String>): GroupEvents()
        data class GetUsersApiCall(val users: List<String>): GroupEvents()
    }

    private val _apiEvents = MutableSharedFlow<GroupEvents>()
    val apiEvents: SharedFlow<GroupEvents> = _apiEvents

    fun findFriends() {
        viewModelScope.launch {
            when(val result = repository.findFriends()) {
                is Resource.Success -> {
                    _apiEvents.emit(GroupEvents.GetFriendsApiCall(result.data ?: listOf()))
                }
                is Resource.Error -> {
                    _apiEvents.emit(GroupEvents.GetFriendsApiCall(listOf()))
                    Log.d("Get friends for user", "${result.message}")
                }
            }
        }
    }

    fun findUsers(username: String) {
        viewModelScope.launch {
            when(val result = repository.findUsers(username)) {
                is Resource.Success -> {
                    _apiEvents.emit(GroupEvents.GetUsersApiCall(result.data ?: listOf()))
                }
                is Resource.Error -> {
                    _apiEvents.emit(GroupEvents.GetUsersApiCall(listOf()))
                }
            }
        }
    }
}