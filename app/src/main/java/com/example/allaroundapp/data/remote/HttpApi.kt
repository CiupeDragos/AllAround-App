package com.example.allaroundapp.data.remote

import com.example.allaroundapp.data.requests.AccountRequest
import com.example.allaroundapp.data.requests.CreateGroupRequest
import com.example.allaroundapp.data.responses.BasicApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
        @Query("username") username: String
    ): Response<List<String>>
}
