package com.example.allaroundapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.databinding.ItemToSelectGroupMemberBinding

class MembersToSelectAdapter: RecyclerView.Adapter<MembersToSelectAdapter.MemberToSelectViewHolder>() {

    class MemberToSelectViewHolder(val binding: ItemToSelectGroupMemberBinding): RecyclerView.ViewHolder(binding.root)

    /*
    private val itemCallback = object: DiffUtil.ItemCallback<SelectedGroupMember>() {
        override fun areItemsTheSame(
            oldItem: SelectedGroupMember,
            newItem: SelectedGroupMember
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SelectedGroupMember,
            newItem: SelectedGroupMember
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, itemCallback)


     */

    var members = listOf<SelectedGroupMember>()

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberToSelectViewHolder {
        val layout = ItemToSelectGroupMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberToSelectViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MemberToSelectViewHolder, position: Int) {
        val item = members[position]
        holder.binding.apply {
            tvUsername.text = item.username
            cardDeleteSelectedMember.visibility = if(item.isSelected) {
                Log.d("Selection debugging", "View now visible")
                View.VISIBLE
            } else {
                Log.d("Selection debugging", "View now gone")
                View.GONE
            }

            root.setOnClickListener {
                onMemberClick?.let { clickMember ->
                    clickMember(item)
                }
            }
        }
    }

    private var onMemberClick: ((SelectedGroupMember) -> Unit)? = null

    fun setOnMemberClick(listener: (SelectedGroupMember) -> Unit) {
        onMemberClick = listener
    }
}