package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemSendFriendRequestBinding
import com.example.allaroundapp.databinding.ItemSentFriendRequestBinding

class SendRequestsAdapter: RecyclerView.Adapter<SendRequestsAdapter.RequestViewHolder>() {

    class RequestViewHolder(val binding: ItemSendFriendRequestBinding): RecyclerView.ViewHolder(binding.root)


    private val itemCallback = object: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, itemCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val layout = ItemSendFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RequestViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.tvUsername.text = item
        holder.binding.tvCancelRequest.setOnClickListener {
            onRequestSendClick?.let { sendRequest ->
                sendRequest(item)
            }
        }

    }

    private var onRequestSendClick: ((String) -> Unit)? = null

    fun setOnRequestSendClick(listener: (String) -> Unit) {
        onRequestSendClick = listener
    }
}