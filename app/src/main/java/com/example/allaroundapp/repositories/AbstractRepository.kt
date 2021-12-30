package com.example.allaroundapp.repositories

import com.example.allaroundapp.other.Resource

interface AbstractRepository {

    suspend fun loginAccount(username: String, password: String): Resource<String>

    suspend fun registerAccount(username: String, password: String): Resource<String>
}