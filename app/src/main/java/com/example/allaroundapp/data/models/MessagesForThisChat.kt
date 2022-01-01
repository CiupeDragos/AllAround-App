package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_MESSAGES_FOR_THIS_CHAT

data class MessagesForThisChat(
    val messages: List<NormalChatMessage>
): BaseModel(TYPE_MESSAGES_FOR_THIS_CHAT)
