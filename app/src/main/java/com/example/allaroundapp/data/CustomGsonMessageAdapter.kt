package com.example.allaroundapp.data

import com.example.allaroundapp.data.models.*
import com.example.allaroundapp.other.Constants.TYPE_CHAT_GROUP_MESSAGE
import com.example.allaroundapp.other.Constants.TYPE_CONNECTED_TO_SOCKET
import com.example.allaroundapp.other.Constants.TYPE_JOIN_CHAT_REQUEST
import com.example.allaroundapp.other.Constants.TYPE_JOIN_GROUP_REQUEST
import com.example.allaroundapp.other.Constants.TYPE_JOIN_MY_CHATS_REQUEST
import com.example.allaroundapp.other.Constants.TYPE_MESSAGES_FOR_THIS_CHAT
import com.example.allaroundapp.other.Constants.TYPE_MESSAGES_FOR_THIS_GROUP
import com.example.allaroundapp.other.Constants.TYPE_NORMAL_CHAT_MESSAGE
import com.example.allaroundapp.other.Constants.TYPE_RECENT_CHATS
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.tinder.scarlet.Message
import com.tinder.scarlet.MessageAdapter
import java.lang.reflect.Type

class CustomGsonMessageAdapter<T> private constructor(
    private val gson: Gson
): MessageAdapter<T> {

    override fun fromMessage(message: Message): T {
        val stringValue = when(message) {
            is Message.Text -> message.value
            is Message.Bytes -> message.value.toString()
        }
        val stringAsJsonObject = JsonParser.parseString(stringValue).asJsonObject
        val type = when(stringAsJsonObject.get("type").asString) {
            TYPE_NORMAL_CHAT_MESSAGE -> NormalChatMessage::class.java
            TYPE_CHAT_GROUP_MESSAGE -> ChatGroupMessage::class.java
            TYPE_MESSAGES_FOR_THIS_CHAT -> MessagesForThisChat::class.java
            TYPE_MESSAGES_FOR_THIS_GROUP -> MessagesForThisGroup::class.java
            TYPE_RECENT_CHATS -> MessagesToSendAsRecent::class.java
            TYPE_CONNECTED_TO_SOCKET -> ConnectedToSocket::class.java
            else -> BaseModel::class.java
        }

        val parsedString = gson.fromJson(stringValue, type)
        return parsedString as T
    }

    override fun toMessage(data: T): Message {
        var dataToSend = data as BaseModel
        dataToSend = when(dataToSend.type) {
            TYPE_JOIN_CHAT_REQUEST -> dataToSend as JoinChatRequest
            TYPE_JOIN_GROUP_REQUEST -> dataToSend as JoinGroupRequest
            TYPE_NORMAL_CHAT_MESSAGE -> dataToSend as NormalChatMessage
            TYPE_CHAT_GROUP_MESSAGE -> dataToSend as ChatGroupMessage
            TYPE_JOIN_MY_CHATS_REQUEST -> dataToSend as JoinMyChatsRequest
            else -> dataToSend
        }
        val jsonDataToSend = gson.toJson(dataToSend)
        return Message.Text(jsonDataToSend)
    }

    class Factory(
        private val gson: Gson
    ): MessageAdapter.Factory {
        override fun create(type: Type, annotations: Array<Annotation>): MessageAdapter<*> {
            return CustomGsonMessageAdapter<Any>(gson)
        }
    }
}