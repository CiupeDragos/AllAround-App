package com.example.allaroundapp.data.models

data class GroupToSendAsRecent(
    val name: String,
    val groupId: String,
    val newMessages: Int,
    val lastMessage: String,
    val owner: String
): RecentChat()
