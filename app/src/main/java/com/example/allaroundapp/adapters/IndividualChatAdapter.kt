package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.data.models.NormalChatMessage
import com.example.allaroundapp.databinding.ItemIncomingMessageBinding
import com.example.allaroundapp.databinding.ItemMessageDayBinding
import com.example.allaroundapp.databinding.ItemOutgoingMessageBinding
import com.example.allaroundapp.other.Constants.MESSAGE_DAY_ANNOUNCEMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.String

const val INCOMING_MESSAGE = 0
const val OUTGOING_MESSAGE = 1
const val TIMESTAMP_MESSAGE = 2

class IndividualChatAdapter(val username: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class IncomingMessageViewHolder(val binding: ItemIncomingMessageBinding): RecyclerView.ViewHolder(binding.root)
    class OutgoingMessageViewHolder(val binding: ItemOutgoingMessageBinding): RecyclerView.ViewHolder(binding.root)
    class MessageForTimeViewHolder(val binding: ItemMessageDayBinding): RecyclerView.ViewHolder(binding.root)

    var chatMessages = listOf<NormalChatMessage>()

    suspend fun updateDataset(newDataset: List<NormalChatMessage>) = withContext(Dispatchers.Default) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return chatMessages.size
            }

            override fun getNewListSize(): Int {
                return newDataset.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chatMessages[oldItemPosition] == newDataset[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return chatMessages[oldItemPosition] == newDataset[newItemPosition]
            }
        })
        withContext(Dispatchers.Main) {
            chatMessages = newDataset
            diff.dispatchUpdatesTo(this@IndividualChatAdapter)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatMessages[position].sender) {
            MESSAGE_DAY_ANNOUNCEMENT -> {
                TIMESTAMP_MESSAGE
            }
            username -> {
                OUTGOING_MESSAGE
            }
            else -> {
                INCOMING_MESSAGE
            }
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TIMESTAMP_MESSAGE -> {
                val layout = ItemMessageDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MessageForTimeViewHolder(layout)
            }
            INCOMING_MESSAGE -> {
                val layout = ItemIncomingMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IncomingMessageViewHolder(layout)
            }
            else -> {
                val layout = ItemOutgoingMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OutgoingMessageViewHolder(layout)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curMessage = chatMessages[position]

        when(holder) {
            is IncomingMessageViewHolder -> {
                holder.binding.apply {
                    tvChatMessage.text = curMessage.message

                    val timestampPattern = SimpleDateFormat("kk:mm", Locale.getDefault())
                    val formattedTime = timestampPattern.format(curMessage.timestamp)

                    tvMessageTimestamp.text = formattedTime
                }
            }
            is OutgoingMessageViewHolder -> {
                holder.binding.apply {
                    tvChatMessage.text = curMessage.message

                    val timestampPattern = SimpleDateFormat("kk:mm", Locale.getDefault())
                    val formattedTime = timestampPattern.format(curMessage.timestamp)

                    tvMessageTimestamp.text = formattedTime
                }
            }
            is MessageForTimeViewHolder -> {
                holder.binding.apply {
                    messageDayText.text = curMessage.message
                }
            }
        }
    }
}