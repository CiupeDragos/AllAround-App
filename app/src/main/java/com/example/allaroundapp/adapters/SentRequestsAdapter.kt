package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemSentFriendRequestBinding

class SentRequestsAdapter: RecyclerView.Adapter<SentRequestsAdapter.RequestViewHolder>() {

    class RequestViewHolder(val binding: ItemSentFriendRequestBinding): RecyclerView.ViewHolder(binding.root)


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
        val layout = ItemSentFriendRequestBinding.inflate(
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
            onRequestCancelClick?.let { cancelRequest ->
                cancelRequest(item)
            }
        }

    }

    private var onRequestCancelClick: ((String) -> Unit)? = null

    fun setOnRequestCancelClick(listener: (String) -> Unit) {
        onRequestCancelClick = listener
    }
}