package com.example.allaroundapp.repositories

import com.example.allaroundapp.other.Resource

interface AbstractRepository {

    suspend fun loginAccount(username: String, password: String): Resource<String>

    suspend fun registerAccount(username: String, password: String): Resource<String>

    suspend fun createGroup(name: String, members: List<String>): Resource<String>

    suspend fun findFriends(): Resource<List<String>>

    suspend fun findUsers(username: String): Resource<List<String>>
}