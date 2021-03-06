package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.data.models.ChatGroupMessage
import com.example.allaroundapp.databinding.ItemIncomingGroupMessageBinding
import com.example.allaroundapp.databinding.ItemMessageDayBinding
import com.example.allaroundapp.databinding.ItemOutgoingGroupMessageBinding
import com.example.allaroundapp.other.Constants.MESSAGE_DAY_ANNOUNCEMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

const val INCOMING_GROUP_MESSAGE = 0
const val OUTGOING_GROUP_MESSAGE = 1
const val TIME_MESSAGE = 2

class GroupChatAdapter(val username: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class IncomingMessageViewHolder(val binding: com.example.allaroundapp.databinding.ItemIncomingGroupMessageBinding): RecyclerView.ViewHolder(binding.root)
    class OutgoingMessageViewHolder(val binding: com.example.allaroundapp.databinding.ItemOutgoingGroupMessageBinding): RecyclerView.ViewHolder(binding.root)
    class MessageDayViewHolder(val binding: com.example.allaroundapp.databinding.ItemMessageDayBinding): RecyclerView.ViewHolder(binding.root)

    var groupChatMessages = listOf<ChatGroupMessage>()

    suspend fun updateDataset(newDataset: List<ChatGroupMessage>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return groupChatMessages.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return groupChatMessages[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return groupChatMessages[oldItemPosition] == newDataset[newItemPosition]
            }
        })
        withContext(Dispatchers.Main) {
            groupChatMessages = newDataset
            diff.dispatchUpdatesTo(this@GroupChatAdapter)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (groupChatMessages[position].sender) {
            MESSAGE_DAY_ANNOUNCEMENT -> {
                TIME_MESSAGE
            }
            username -> {
                OUTGOING_GROUP_MESSAGE
            }
            else -> {
                INCOMING_GROUP_MESSAGE
            }
        }
    }

    override fun getItemCount(): Int {
        return groupChatMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TIME_MESSAGE -> {
                val layout = ItemMessageDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MessageDayViewHolder(layout)
            }
            INCOMING_GROUP_MESSAGE -> {
                val layout = ItemIncomingGroupMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IncomingMessageViewHolder(layout)
            }
            else -> {
                val layout = ItemOutgoingGroupMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OutgoingMessageViewHolder(layout)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curMessage = groupChatMessages[position]

        when(holder) {
            is IncomingMessageViewHolder -> {
                holder.binding.apply {
                    tvSenderUsername.text = curMessage.sender
                    tvChatMessage.text = curMessage.message

                    val timestampFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
                    val formattedTime = timestampFormat.format(curMessage.timestamp)

                    tvMessageTimestamp.text = formattedTime
                }
            }
            is OutgoingMessageViewHolder -> {
                holder.binding.apply {
                    tvChatMessage.text = curMessage.message

                    val timestampFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
                    val formattedTime = timestampFormat.format(curMessage.timestamp)

                    tvMessageTimestamp.text = formattedTime
                }
            }
            is MessageDayViewHolder -> {
                holder.binding.messageDayText.text = curMessage.message
            }
        }
    }
}