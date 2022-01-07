package com.example.allaroundapp.repositories

import com.example.allaroundapp.data.remote.HttpApi
import com.example.allaroundapp.data.requests.AccountRequest
import com.example.allaroundapp.data.requests.CreateGroupRequest
import com.example.allaroundapp.data.responses.FriendRequestsResponse
import com.example.allaroundapp.other.Resource
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.Exception

class DefaultRepositoryImpl(private val httpApi: HttpApi): AbstractRepository {
    override suspend fun registerAccount(username: String, password: String): Resource<String> {
        val accountRequest = AccountRequest(username, password)
        val result = try {
            val networkCall = httpApi.registerAccount(accountRequest)
            if(networkCall.isSuccessful && networkCall.body()!!.successful) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()?.message ?: networkCall.message())
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
                Resource.Error(networkCall.body()?.message ?: networkCall.message())
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Network error,check your internet connection")
        }
        return result
    }

    override suspend fun createGroup(name: String, members: List<String>): Resource<String> {
        val createGroupRequest = CreateGroupRequest(name, members)
        val result = try {
            val networkCall = httpApi.createGroup(createGroupRequest)
            if(networkCall.isSuccessful && networkCall.body()!!.successful) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()?.message ?: networkCall.message())
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun findFriends(): Resource<List<String>> {
        val result = try {
            val networkCall = httpApi.findFriends()
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun findUsers(username: String): Resource<List<String>> {
        val result = try {
            val networkCall = httpApi.findUsers(username)
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun getFriendRequests(): Resource<FriendRequestsResponse> {
        val result = try {
            val networkCall = httpApi.getFriendRequests()
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun sendFriendRequest(sentToUsername: String): Resource<String> {
        val result = try {
            val networkCall = httpApi.sendFriendRequest(sentToUsername)
            if(networkCall.isSuccessful && networkCall.body()!!.successful) {
                Resource.Success(networkCall.body()!!.message)
            } else if(networkCall.isSuccessful) {
                Resource.Error(networkCall.body()!!.message)
            }
            else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun cancelFriendRequest(sentToUsername: String): Resource<String> {
        val result = try {
            val networkCall = httpApi.cancelFriendRequest(sentToUsername)
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!.message)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun refuseFriendRequest(senderUsername: String): Resource<String> {
        val result = try {
            val networkCall = httpApi.refuseFriendRequest(senderUsername)
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!.message)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }

    override suspend fun acceptFriendRequest(senderUsername: String): Resource<String> {
        val result = try {
            val networkCall = httpApi.acceptFriendRequest(senderUsername)
            if(networkCall.isSuccessful) {
                Resource.Success(networkCall.body()!!.message)
            } else {
                Resource.Error(networkCall.message())
            }
        } catch (e: Exception) {
            Resource.Error("Error.Check your internet connection")
        }
        return result
    }
}