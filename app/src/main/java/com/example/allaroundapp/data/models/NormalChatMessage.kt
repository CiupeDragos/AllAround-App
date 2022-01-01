package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_NORMAL_CHAT_MESSAGE

data class NormalChatMessage(
    val message: String,
    val sender: String,
    val receiver: String,
    val timestamp: Long
): BaseModel(TYPE_NORMAL_CHAT_MESSAGE)
