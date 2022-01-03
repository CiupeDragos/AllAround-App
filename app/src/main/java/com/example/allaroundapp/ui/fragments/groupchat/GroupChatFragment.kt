package com.example.allaroundapp.ui.fragments.groupchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.GroupChatAdapter
import com.example.allaroundapp.data.models.ChatGroupMessage
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentFriendsBinding
import com.example.allaroundapp.databinding.FragmentGroupChatBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment: Fragment() {
    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val args: GroupChatFragmentArgs by navArgs()

    private var updateGroupChatMessagesJob: Job? = null

    private lateinit var groupAdapter: GroupChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToUiUpdates()
        observeBaseModels()
        connectToGroupChat()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar).visibility =
            View.GONE

        binding.btnSendMessage.setOnClickListener {
            viewModel.sendGroupMessage(
                args.username,
                args.groupId,
                binding.etGroupChatMessage.text.toString()
            )
            binding.etGroupChatMessage.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeBaseModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.socketEvents.collect { baseModel ->
                when(baseModel) {
                    is ConnectedToSocket -> {
                        connectToGroupChat()
                    }
                }
            }
        }
    }

    private fun subscribeToUiUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groupMessages.collect { messages ->
                    val canScrollDown = binding.rvGroupChatMessages.canScrollVertically(1)
                    updateGroupMessages(messages)
                    updateGroupChatMessagesJob?.join()
                    if(!canScrollDown) {
                        binding.rvGroupChatMessages.scrollToPosition(
                            groupAdapter.groupChatMessages.size - 1
                        )
                    }
                }
            }
        }
    }

    private fun updateGroupMessages(messages: List<ChatGroupMessage>) {
        updateGroupChatMessagesJob?.cancel()
        updateGroupChatMessagesJob = lifecycleScope.launch {
            groupAdapter.updateDataset(messages)
        }
    }

    private fun setupRecyclerView() {
        groupAdapter = GroupChatAdapter(args.username)
        binding.rvGroupChatMessages.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun connectToGroupChat() {
        binding.tvChatPartner.text = args.groupName
        viewModel.sendJoinGroupRequest(args.username, args.groupId)
    }
}