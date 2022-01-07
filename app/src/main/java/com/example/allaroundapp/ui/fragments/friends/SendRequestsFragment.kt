package com.example.allaroundapp.ui.fragments.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.adapters.SendRequestsAdapter
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.databinding.FragmentProfileBinding
import com.example.allaroundapp.databinding.FragmentSendRequestsBinding
import com.example.allaroundapp.other.customSnackbar
import com.example.allaroundapp.other.hideKeyboard
import com.example.allaroundapp.ui.fragments.friends.FriendsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendRequestsFragment : Fragment() {
    private var _binding: FragmentSendRequestsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendsViewModel by viewModels()

    private lateinit var userAdapter: SendRequestsAdapter

    private var searchUsersJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        observeEvents()

        binding.etSearchUsers.addTextChangedListener { text ->
            searchUsers(text.toString())
        }

        userAdapter.setOnRequestSendClick { sendToUsername ->
            sendFriendRequest(sendToUsername)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionEvents.collect { event ->
                    when (event) {
                        is FriendsViewModel.ActionEvents.RequestEvent -> {
                            customSnackbar(event.data)
                        }
                        is FriendsViewModel.ActionEvents.SearchEvent -> {
                            val list = event.data
                            if(list.isEmpty()) {
                                binding.tvNoUsersFound.visibility = View.VISIBLE
                            } else {
                                userAdapter.differ.submitList(list)
                            }
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun searchUsers(username: String) {
        if(userAdapter.differ.currentList.isNotEmpty()) {
            userAdapter.differ.submitList(listOf())
        }
        searchUsersJob?.cancel()
        searchUsersJob = lifecycleScope.launch {
            if(username.trim().isNotEmpty()) {
                binding.tvNoUsersFound.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                delay(500L)
                findUsers(username)
            } else {
                binding.tvNoUsersFound.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun sendFriendRequest(sentToUsername: String) {
        viewModel.sendFriendRequest(sentToUsername)
        requireActivity().hideKeyboard(binding.root)
    }

    private fun findUsers(username: String) {
        viewModel.findUsers(username)
    }

    private fun setupRecycleView() {
        userAdapter = SendRequestsAdapter()
        binding.rvFoundUsers.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}