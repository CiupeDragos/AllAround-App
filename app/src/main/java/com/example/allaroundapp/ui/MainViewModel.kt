package com.example.allaroundapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.data.models.BaseModel
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.data.models.JoinMyChatsRequest
import com.example.allaroundapp.data.models.MessagesToSendAsRecent
import com.example.allaroundapp.data.remote.WebSocketApi
import com.example.allaroundapp.other.Constants.CHATS_FRAGMENT
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val webSocketApi: WebSocketApi
) : ViewModel() {

    private val _recentMessages = MutableStateFlow(MessagesToSendAsRecent(listOf(), listOf()))
    val recentMessages: StateFlow<MessagesToSendAsRecent> = _recentMessages

    private val _connectionEvents = MutableSharedFlow<WebSocket.Event>()
    val connectionEvents: SharedFlow<WebSocket.Event> = _connectionEvents

    private val _socketEvents = MutableSharedFlow<BaseModel>()
    val socketEvents: SharedFlow<BaseModel> = _socketEvents

    init {
        observeConnectionEvents()
        observeBaseModels()
    }

    private fun observeConnectionEvents() {
        viewModelScope.launch {
            webSocketApi.observeEvents().collect { event ->
                _connectionEvents.emit(event)
            }
        }
    }

    private fun observeBaseModels() {
        viewModelScope.launch {
            webSocketApi.observeBaseModels().collect { baseModel ->
                when (baseModel) {
                    is MessagesToSendAsRecent -> {
                        Log.d("Recent chats", "Recent chats arrived")
                        _recentMessages.value = baseModel
                    }
                    is ConnectedToSocket -> {
                        Log.d("Recent chats", "Connected to chat received")
                        _socketEvents.emit(baseModel)
                    }
                }
            }
        }
    }

    private fun sendBaseModel(baseModel: BaseModel) {
        webSocketApi.sendBaseModel(baseModel)
    }

    fun sendJoinMyChatsRequest(username: String) {
        viewModelScope.launch {
            val request = JoinMyChatsRequest(username)
            sendBaseModel(request)
        }
    }
}