package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_CHAT_GROUP_MESSAGE

data class ChatGroupMessage(
    val message: String,
    val sender: String,
    val groupId: String,
    val timestamp: Long
): BaseModel(TYPE_CHAT_GROUP_MESSAGE)
