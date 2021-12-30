package com.example.allaroundapp.data

import com.example.allaroundapp.other.Constants.IGNORE_AUTH_URL
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor: Interceptor {

    var username: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if(request.url.encodedPath in IGNORE_AUTH_URL){
            return chain.proceed(request)
        }
        val url = request.url.newBuilder()
            .addQueryParameter("username", username ?: "")
            .build()
        val newRequest = request.newBuilder()
            .url(url)
            .build()
        return chain.proceed(newRequest)
    }
}