package com.example.allaroundapp.data.models

data class ChatToSendAsRecent(
    val chattingTo: String,
    val newMessages: Int,
    val lastMessage: String,
): RecentChat()
