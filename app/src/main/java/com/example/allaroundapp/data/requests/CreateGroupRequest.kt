package com.example.allaroundapp.data.requests

data class CreateGroupRequest(
    val name: String,
    val members: List<String>
)
