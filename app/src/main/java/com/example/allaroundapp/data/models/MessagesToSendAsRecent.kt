package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_RECENT_CHATS

data class MessagesToSendAsRecent(
    val chats: List<ChatToSendAsRecent>,
    val groups: List<GroupToSendAsRecent>
): BaseModel(TYPE_RECENT_CHATS)
