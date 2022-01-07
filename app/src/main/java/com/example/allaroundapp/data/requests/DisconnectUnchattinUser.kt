package com.example.allaroundapp.data.requests

import com.example.allaroundapp.data.models.BaseModel
import com.example.allaroundapp.other.Constants.TYPE_DISCONNECT_UNCHATTING_USER

data class DisconnectUnchattinUser(
    val username: String
): BaseModel(TYPE_DISCONNECT_UNCHATTING_USER)
