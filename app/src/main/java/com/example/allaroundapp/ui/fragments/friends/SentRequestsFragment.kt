package com.example.allaroundapp.ui.fragments.friends

import android.os.Bundle
import android.util.Log
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
import com.example.allaroundapp.adapters.SentRequestsAdapter
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.databinding.FragmentProfileBinding
import com.example.allaroundapp.databinding.FragmentSentRequestsBinding
import com.example.allaroundapp.other.customSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SentRequestsFragment: Fragment() {
    private var _binding: FragmentSentRequestsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FriendsViewModel by viewModels()
    private val args: SentRequestsFragmentArgs by navArgs()

    private lateinit var requestsAdapter: SentRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToUiUpdates()
        observeEvents()
        getSentRequests()

        requestsAdapter.setOnRequestCancelClick { sentToUsername ->
            cancelFriendRequest(sentToUsername)
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
                    Log.d("observed", "Observed for list $list")
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

    private fun getSentRequests() {
        viewModel.getSentRequests()
    }

    private fun setupRecyclerView() {
        requestsAdapter = SentRequestsAdapter()
        binding.rvSentRequests.apply {
            adapter = requestsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun cancelFriendRequest(sentToUsername: String) {
        viewModel.cancelFriendRequest(sentToUsername)
    }
}