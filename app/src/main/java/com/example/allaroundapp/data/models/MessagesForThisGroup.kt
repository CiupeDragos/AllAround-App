package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_MESSAGES_FOR_THIS_GROUP

data class MessagesForThisGroup(
    val messages: List<ChatGroupMessage>
): BaseModel(TYPE_MESSAGES_FOR_THIS_GROUP)
