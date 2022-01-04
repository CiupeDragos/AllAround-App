package com.example.allaroundapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemSelectedGroupMemberBinding
import kotlin.String

class SelectedMembersAdapter :
    RecyclerView.Adapter<SelectedMembersAdapter.SelectedMembersViewHolder>() {

    class SelectedMembersViewHolder(val binding: ItemSelectedGroupMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val itemCallback = object : DiffUtil.ItemCallback<String>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMembersViewHolder {
        val layout = ItemSelectedGroupMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectedMembersViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SelectedMembersViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.apply {
            tvUsername.text = item

            root.setOnClickListener {
                onSelectedMemberClick?.let { clickMember ->
                    clickMember(item)
                }
            }
        }
    }

    private var onSelectedMemberClick: ((String) -> Unit)? = null

    fun setOnSelectedMemberClick(listener: (String) -> Unit) {
        onSelectedMemberClick = listener
    }
}