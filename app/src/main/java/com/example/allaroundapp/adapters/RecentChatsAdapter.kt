package com.example.allaroundapp.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.R
import com.example.allaroundapp.data.models.ChatToSendAsRecent
import com.example.allaroundapp.data.models.GroupToSendAsRecent
import com.example.allaroundapp.data.models.RecentChat
import com.example.allaroundapp.databinding.RecentChatItemBinding
import com.example.allaroundapp.other.Constants.TYPE_CHAT
import com.example.allaroundapp.other.Constants.TYPE_GROUP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type


class RecentChatsAdapter(
    val username: String,
    val context: Context
) : RecyclerView.Adapter<RecentChatsAdapter.RecentChatViewHolder>() {

    class RecentChatViewHolder(
        val binding: RecentChatItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var recentChats = listOf<RecentChat>()

    suspend fun updateDataset(newDataset: List<RecentChat>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return recentChats.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return recentChats[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return recentChats[oldItemPosition] == newDataset[newItemPosition]
            }
        })
        withContext(Dispatchers.Main) {
            recentChats = newDataset
            diff.dispatchUpdatesTo(this@RecentChatsAdapter)
        }
    }

    override fun getItemCount(): Int {
        return recentChats.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val layout = RecentChatItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecentChatViewHolder(layout)

    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        val curItem = recentChats[position]
        when (curItem.typeOfChat) {
            TYPE_CHAT -> {
                val curChat = curItem as ChatToSendAsRecent
                holder.binding.apply {
                    tvChatPartner.text = curChat.chattingTo
                    if (curChat.newMessages == 0) {
                        val lastMessageText = setLastMessage(
                            username,
                            curChat.lastMessage,
                            curChat.lastMessageSender
                        )
                        tvNewMessages.text = lastMessageText
                        tvNewMessages.setTypeface(tvNewMessages.typeface, Typeface.NORMAL)
                        tvChatPartner.setTypeface(tvChatPartner.typeface, Typeface.NORMAL)
                    } else {
                        tvNewMessages.text = context.resources
                            .getString(
                                R.string.user_has_new_messages,
                                curChat.newMessages
                            )
                        tvNewMessages.setTypeface(tvNewMessages.typeface, Typeface.BOLD)
                        tvChatPartner.setTypeface(tvChatPartner.typeface, Typeface.BOLD)
                    }
                    root.setOnClickListener {
                        onChatClick?.let { clickChat ->
                            clickChat(curChat)
                        }
                    }
                }
            }
            TYPE_GROUP -> {
                val curGroup = curItem as GroupToSendAsRecent
                holder.binding.apply {
                    chatPhoto.setImageResource(R.drawable.no_group_image)
                    tvChatPartner.text = curGroup.name
                    if(curGroup.newMessages == 0) {
                        val lastMessageText = setLastMessage(
                            username,
                            curGroup.lastMessage,
                            curGroup.lastMessageSender
                        )
                        tvNewMessages.text = lastMessageText
                        tvNewMessages.setTypeface(tvNewMessages.typeface, Typeface.NORMAL)
                        tvChatPartner.setTypeface(tvChatPartner.typeface, Typeface.NORMAL)
                    } else {
                        tvNewMessages.text = context.resources
                            .getString(
                                R.string.user_has_new_messages,
                                curGroup.newMessages
                            )
                        tvNewMessages.setTypeface(tvNewMessages.typeface, Typeface.BOLD)
                        tvChatPartner.setTypeface(tvChatPartner.typeface, Typeface.BOLD)
                    }
                    root.setOnClickListener {
                        onGroupClick?.let { clickGroup ->
                            clickGroup(curGroup)
                        }
                    }
                }
            }
        }
    }

    private fun setLastMessage(
        username: String,
        lastMessage: String,
        lastMessageSender: String
    ): String {
        return if (username == lastMessageSender) {
            context.resources
                .getString(
                    R.string.user_sent_last_message,
                    lastMessage
                )
        } else if(username != lastMessageSender && lastMessageSender != "No message") {
            context.resources
                .getString(
                    R.string.chat_partner_send_last_message,
                    lastMessageSender,
                    lastMessage
                )
        } else {
            "No messages"
        }
    }

    var onChatClick: ((ChatToSendAsRecent) -> Unit)? = null
    var onGroupClick: ((GroupToSendAsRecent) -> Unit)? = null

    fun setOnChatClickListener(listener: (ChatToSendAsRecent) -> Unit) {
        onChatClick = listener
    }

    fun setOnGroupClickListener(listener: (GroupToSendAsRecent) -> Unit) {
        onGroupClick = listener
    }
}