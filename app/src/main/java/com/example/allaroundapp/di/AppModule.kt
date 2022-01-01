package com.example.allaroundapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.allaroundapp.data.BasicAuthInterceptor
import com.example.allaroundapp.data.CustomGsonMessageAdapter
import com.example.allaroundapp.data.FlowStreamAdapter
import com.example.allaroundapp.data.remote.HttpApi
import com.example.allaroundapp.data.remote.WebSocketApi
import com.example.allaroundapp.other.Constants.BASE_URL
import com.example.allaroundapp.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import com.example.allaroundapp.other.Constants.RECONNECT_INTERVAL
import com.example.allaroundapp.other.Constants.WS_BASE_URL
import com.example.allaroundapp.repositories.AbstractRepository
import com.example.allaroundapp.repositories.DefaultRepositoryImpl
import com.google.gson.Gson
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGsonInstance() = Gson()

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @Singleton
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideHttpApi(
        okHttpClient: OkHttpClient
    ): HttpApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(HttpApi::class.java)
    }

    @Singleton
    @Provides
    fun provideWebSocketApi(
        gson: Gson,
        app: Application,
        okHttpClient: OkHttpClient
    ): WebSocketApi {
        return Scarlet.Builder()
            .backoffStrategy(LinearBackoffStrategy(RECONNECT_INTERVAL))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(app))
            .webSocketFactory(
                okHttpClient.newWebSocketFactory(WS_BASE_URL)
            )
            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
            .addMessageAdapterFactory(CustomGsonMessageAdapter.Factory(gson))
            .build()
            .create()
    }

    @Singleton
    @Provides
    fun provideDefaultRepImpl(httpApi: HttpApi): AbstractRepository =
        DefaultRepositoryImpl(httpApi)

    @Singleton
    @Provides
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}