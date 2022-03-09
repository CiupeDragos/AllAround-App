package com.example.allaroundapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allaroundapp.data.models.*
import com.example.allaroundapp.data.remote.WebSocketApi
import com.example.allaroundapp.data.requests.DisconnectUnchattinUser
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class MainViewModel @Inject constructor(
    private val webSocketApi: WebSocketApi
) : ViewModel() {

    private val _recentMessages = MutableStateFlow(MessagesToSendAsRecent(listOf(), listOf()))
    val recentMessages: StateFlow<MessagesToSendAsRecent> = _recentMessages

    private val _individualMessages = MutableStateFlow(listOf<NormalChatMessage>())
    val individualMessages: StateFlow<List<NormalChatMessage>> = _individualMessages

    private val _groupMessages = MutableStateFlow(listOf<ChatGroupMessage>())
    val groupMessages: StateFlow<List<ChatGroupMessage>> = _groupMessages

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
                    is MessagesForThisChat -> {
                        _individualMessages.value = baseModel.messages
                    }
                    is MessagesForThisGroup -> {
                        _groupMessages.value = baseModel.messages
                    }
                    is ConnectedToSocket -> {
                        Log.d("Recent chats", "Connected to chat received")
                        _socketEvents.emit(baseModel)
                    }
                    is NormalChatMessage -> {
                        _individualMessages.value += baseModel
                    }
                    is ChatGroupMessage -> {
                        _groupMessages.value += baseModel
                    }
                }
            }
        }
    }

    fun sendChatMessage(sender: String, receiver: String, message: String) {
        if(message.trim().isEmpty()) {
            Log.d("Send message", "Message sending aborted")
            return
        }
        val messageToSend = NormalChatMessage(message, sender, receiver, System.currentTimeMillis())
        sendBaseModel(messageToSend)
    }

    fun sendGroupMessage(sender: String, groupId: String, message: String) {
        if(message.trim().isEmpty()) {
            return
        }
        val messageToSend = ChatGroupMessage(message, sender, groupId, System.currentTimeMillis())
        sendBaseModel(messageToSend)
    }

    private fun sendBaseModel(baseModel: BaseModel) {
        webSocketApi.sendBaseModel(baseModel)
    }

    fun sendJoinMyChatsRequest(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = JoinMyChatsRequest(username)
            sendBaseModel(request)
        }
    }

    fun sendJoinChatRequest(username: String, chatPartner: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = JoinChatRequest(username, chatPartner)
            sendBaseModel(request)
        }
    }

    fun sendJoinGroupRequest(username: String, groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = JoinGroupRequest(username, groupId)
            sendBaseModel(request)
        }
    }

    fun disconnectUnchattingUser(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = DisconnectUnchattinUser(username)
            sendBaseModel(request)
        }
    }

    fun getMessageDetailsFromTimestamp(timestamp: Long): HashMap<String, String> {
        val yearFormatPattern = SimpleDateFormat("yyy", Locale.getDefault())
        val monthFormatPattern = SimpleDateFormat("LLL", Locale.getDefault())
        val dayFormatPattern = SimpleDateFormat("d", Locale.getDefault())

        val formattedYear = yearFormatPattern.format(timestamp)
        val formattedMonth = monthFormatPattern.format(timestamp)
        val formattedDay = dayFormatPattern.format(timestamp)

        val hashMapToReturn = hashMapOf<String, String>()

        hashMapToReturn.apply {
            put("year", formattedYear)
            put("month", formattedMonth)
            put("day", formattedDay)
        }

        return hashMapToReturn
    }

    fun formatMessageTimestamp(timestamp1: Long, timestamp2: Long): String {
        val detForTimestamp1 = getMessageDetailsFromTimestamp(timestamp1)
        val detForTimestamp2 = getMessageDetailsFromTimestamp(timestamp2)
        val detForCurrentTimestamp = getMessageDetailsFromTimestamp(System.currentTimeMillis())
        val year1 = detForTimestamp1["year"]
        val year2 = detForTimestamp2["year"]
        val month1 = detForTimestamp1["month"]
        val month2 = detForTimestamp2["month"]
        val day1 = detForTimestamp1["day"]
        val day2 = detForTimestamp2["day"]
        val dayCur = detForCurrentTimestamp["day"]
        val yearCur = detForCurrentTimestamp["year"]

        var formattedDate = ""

        when {
            year1 != year2 -> {
                formattedDate = if(year2 == yearCur) {
                    "$month2 $day2"
                } else {
                    "$month2 $day2 $year2"
                }
            }
            month1 != month2 -> {
                formattedDate = "$month2 $day2"
            }
            day1 != day2 -> {
                when {
                    day2!!.toInt() == dayCur!!.toInt() - 1 -> {
                        formattedDate = "Yesterday"
                    }
                    day2 == dayCur -> {
                        formattedDate = "Today"
                    }
                    else -> {
                        formattedDate = "$month2 $day2"
                    }
                }
            }
            else -> {
                formattedDate = "NO_HANDLE"
            }
        }
        return formattedDate
    }
}