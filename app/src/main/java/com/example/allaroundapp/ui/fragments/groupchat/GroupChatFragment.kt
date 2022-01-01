package com.example.allaroundapp.ui.fragments.groupchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentFriendsBinding
import com.example.allaroundapp.databinding.FragmentGroupChatBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding

class GroupChatFragment: Fragment() {
    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}