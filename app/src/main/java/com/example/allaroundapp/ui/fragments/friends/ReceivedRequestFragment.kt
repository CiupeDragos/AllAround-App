package com.example.allaroundapp.ui.fragments.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.adapters.ReceivedRequestsAdapter
import com.example.allaroundapp.databinding.*
import com.example.allaroundapp.other.customSnackbar
import com.example.allaroundapp.other.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReceivedRequestFragment: Fragment() {
    private var _binding: FragmentReceivedRequestsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendsViewModel by viewModels()
    private val args: ReceivedRequestFragmentArgs by navArgs()

    private lateinit var requestsAdapter: ReceivedRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceivedRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToUiUpdates()
        observeEvents()
        getReceivedRequests()

        requestsAdapter.setOnRequestDenyClick { senderUsername ->
            denyFriendRequest(senderUsername)
        }
        requestsAdapter.setOnRequestAcceptClick { senderUsername ->
            acceptFriendRequest(senderUsername)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeToUiUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiEvents.collect { list ->
                    if(list.isNotEmpty()) {
                        binding.tvNoRequests.visibility = View.GONE
                    } else {
                        binding.tvNoRequests.visibility = View.VISIBLE
                    }
                    requestsAdapter.differ.submitList(list)
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionEvents.collect { event ->
                    when(event) {
                        is FriendsViewModel.ActionEvents.RequestEvent -> {
                            customSnackbar(event.data)
                        }
                    }
                }
            }
        }
    }

    private fun denyFriendRequest(senderUsername: String) {
        viewModel.denyFriendRequest(senderUsername)
    }

    private fun acceptFriendRequest(senderUsername: String) {
        viewModel.acceptFriendRequest(senderUsername)
    }


    private fun getReceivedRequests() {
        viewModel.getReceivedRequests()
    }

    private fun setupRecyclerView() {
        requestsAdapter = ReceivedRequestsAdapter()
        binding.rvReceivedRequests.apply {
            adapter = requestsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}