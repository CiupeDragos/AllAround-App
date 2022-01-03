package com.example.allaroundapp.ui.fragments.creategroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentCreateGroupBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.databinding.FragmentProfileBinding

class CreateGroupFragment: Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    private val args: CreateGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}