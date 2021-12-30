package com.example.allaroundapp.repositories

import com.example.allaroundapp.data.remote.HttpApi
import com.example.allaroundapp.data.requests.AccountRequest
import com.example.allaroundapp.other.Resource
import kotlin.Exception

class DefaultRepositoryImpl(val httpApi: HttpApi): AbstractRepository {
    override suspend fun registerAccount(username: String, password: String): Resource<String> {
        val accountRequest = AccountRequest(username, password)
        val result = try {
            val networkCall = httpApi.registerAccount(accountRequest)
            if(networkCall.isSuccessful && networkCall.body()!!.successful) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()!!.message)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Network error,check your internet connection")
        }
        return result
    }


    override suspend fun loginAccount(username: String, password: String): Resource<String> {
        val accountRequest = AccountRequest(username, password)
        val result = try {
            val networkCall = httpApi.loginAccount(accountRequest)
            if(networkCall.isSuccessful && networkCall.body()!!.successful) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()!!.message)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Network error,check your internet connection")
        }
        return result
    }


}