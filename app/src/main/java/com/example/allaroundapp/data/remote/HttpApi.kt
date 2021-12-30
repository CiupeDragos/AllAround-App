package com.example.allaroundapp.data.remote

import com.example.allaroundapp.data.requests.AccountRequest
import com.example.allaroundapp.data.responses.BasicApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HttpApi {

    @POST("/registerAccount")
    suspend fun registerAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>

    @POST("/loginAccount")
    suspend fun loginAccount(
        @Body accountRequest: AccountRequest
    ): Response<BasicApiResponse>
}
