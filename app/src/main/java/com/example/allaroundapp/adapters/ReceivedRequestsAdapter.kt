package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemReceivedFriendRequestBinding

class ReceivedRequestsAdapter: RecyclerView.Adapter<ReceivedRequestsAdapter.FriendViewHolder>() {

    class FriendViewHolder(val binding: ItemReceivedFriendRequestBinding): RecyclerView.ViewHolder(binding.root)


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val layout = ItemReceivedFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(layout)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvUsername.text = item

            tvDenyFriendRequest.setOnClickListener {
                onRequestDenyClick?.let { denyFriendRequest ->
                    denyFriendRequest(item)
                }
            }
            tvAcceptFriendRequest.setOnClickListener {
                onRequestAcceptClick?.let { acceptFriendRequest ->
                    acceptFriendRequest(item)
                }
            }
        }
    }

    private var onRequestDenyClick: ((String) -> Unit)? = null

    private var onRequestAcceptClick: ((String) -> Unit)? = null

    fun setOnRequestDenyClick(listener: (String) -> Unit) {
        onRequestDenyClick = listener
    }

    fun setOnRequestAcceptClick(listener: (String) -> Unit) {
        onRequestAcceptClick = listener
    }
}