package com.example.allaroundapp.data.remote

import com.example.allaroundapp.data.requests.AccountRequest
import com.example.allaroundapp.data.requests.CreateGroupRequest
import com.example.allaroundapp.data.responses.BasicApiResponse
import com.example.allaroundapp.data.responses.FriendRequestsResponse
import retrofit2.Response
import retrofit2.http.*

interface HttpApi {

    @POST("/registerAccount")
    suspend fun registerAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>
    
    @POST("/loginAccount")
    suspend fun loginAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>

    @POST("/createChatGroup")
    suspend fun createGroup(
        @Body createGroupRequest: CreateGroupRequest
    ): Response<BasicApiResponse>

    @GET("/findFriends")
    suspend fun findFriends(): Response<List<String>>

    @GET("/findUsers")
    suspend fun findUsers(
        @Query("usernameToSearch") username: String
    ): Response<List<String>>

    @GET("/getFriendRequests")
    suspend fun getFriendRequests(): Response<FriendRequestsResponse>

    @POST("/sendFriendRequest")
    suspend fun sendFriendRequest(
        @Query("sentToUsername") sentToUsername: String
    ): Response<BasicApiResponse>

    @POST("/cancelFriendRequest")
    suspend fun cancelFriendRequest(
        @Query("sentToUsername") sentToUsername: String
    ): Response<BasicApiResponse>

    @POST("/refuseFriendRequest")
    suspend fun refuseFriendRequest(
        @Query("senderUsername") sentToUsername: String
    ): Response<BasicApiResponse>

    @POST("/acceptFriendRequest")
    suspend fun acceptFriendRequest(
        @Query("senderUsername") sentToUsername: String
    ): Response<BasicApiResponse>
}
