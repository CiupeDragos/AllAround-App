package com.example.allaroundapp.data.models

abstract class RecentChat(
    val lastMessageTimestamp: Long = 0,
    val typeOfChat: String = "",
    val lastMessageSender: String = ""
)