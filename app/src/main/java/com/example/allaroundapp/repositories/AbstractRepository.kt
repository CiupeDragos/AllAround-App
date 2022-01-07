package com.example.allaroundapp.repositories

import com.example.allaroundapp.data.responses.FriendRequestsResponse
import com.example.allaroundapp.other.Resource

interface AbstractRepository {

    suspend fun loginAccount(username: String, password: String): Resource<String>

    suspend fun registerAccount(username: String, password: String): Resource<String>

    suspend fun createGroup(name: String, members: List<String>): Resource<String>

    suspend fun findFriends(): Resource<List<String>>

    suspend fun findUsers(username: String): Resource<List<String>>

    suspend fun getFriendRequests(): Resource<FriendRequestsResponse>

    suspend fun sendFriendRequest(sentToUsername: String): Resource<String>

    suspend fun cancelFriendRequest(sentToUsername: String): Resource<String>

    suspend fun refuseFriendRequest(senderUsername: String): Resource<String>

    suspend fun acceptFriendRequest(senderUsername: String): Resource<String>
}