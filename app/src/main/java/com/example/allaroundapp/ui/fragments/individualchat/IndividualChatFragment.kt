package com.example.allaroundapp.ui.fragments.individualchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.allaroundapp.R
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentFriendsBinding
import com.example.allaroundapp.databinding.FragmentIndividualChatBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class IndividualChatFragment: Fragment() {
    private var _binding: FragmentIndividualChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndividualChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar).visibility =
            View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}