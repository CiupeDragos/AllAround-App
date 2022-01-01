package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_JOIN_CHAT_REQUEST

data class JoinChatRequest(
    val username: String,
    val chatPartner: String
): BaseModel(TYPE_JOIN_CHAT_REQUEST)
