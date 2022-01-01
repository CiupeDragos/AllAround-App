package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_JOIN_MY_CHATS_REQUEST

data class JoinMyChatsRequest(
    val username: String
): BaseModel(TYPE_JOIN_MY_CHATS_REQUEST)
