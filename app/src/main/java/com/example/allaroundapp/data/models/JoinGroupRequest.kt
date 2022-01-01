package com.example.allaroundapp.data.models

import com.example.allaroundapp.other.Constants.TYPE_JOIN_GROUP_REQUEST

data class JoinGroupRequest(
    val username: String,
    val groupId: String
): BaseModel(TYPE_JOIN_GROUP_REQUEST)
