package com.example.allaroundapp.other

object Constants {

    const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"

    const val BASE_URL = "http://192.168.0.107:8080"

    const val KEY_LOGIN_USERNAME = "KEY_LOGIN_USERNAME"
    const val KEY_PASSWORD = "KEY_PASSWORD"
    const val NO_USERNAME = "NO_USERNAME"
    const val NO_PASSWORD = "NO_PASSWORD"

    const val MIN_USERNAME_LENGTH = 4
    const val MAX_USERNAME_LENGTH = 16
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 32
    const val MIN_CHAT_GROUP_NAME_LENGTH = 6
    const val MAX_CHAT_GROUP_NAME_LENGTH = 16

    const val TYPE_JOIN_CHAT_REQUEST = "TYPE_JOIN_CHAT_REQUEST"
    const val TYPE_JOIN_GROUP_REQUEST = "TYPE_JOIN_GROUP_REQUEST"
    const val TYPE_NORMAL_CHAT_MESSAGE = "TYPE_NORMAL_CHAT_MESSAGE"
    const val TYPE_CHAT_GROUP_MESSAGE = "TYPE_CHAT_GROUP_MESSAGE"

    val IGNORE_AUTH_URL = listOf("/registerAccount", "/loginAccount")

}