package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemToSelectGroupMemberBinding

class FriendsAdapter: RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    class FriendViewHolder(val binding: ItemToSelectGroupMemberBinding): RecyclerView.ViewHolder(binding.root)


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
        val layout = ItemToSelectGroupMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(layout)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.tvUsername.text = item

    }

    private var onFriendClick: ((String) -> Unit)? = null

    fun setOnFriendClick(listener: (String) -> Unit) {
        onFriendClick = listener
    }
}