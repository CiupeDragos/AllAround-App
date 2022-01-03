package com.example.allaroundapp.ui.fragments.individualchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.IndividualChatAdapter
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.data.models.NormalChatMessage
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentFriendsBinding
import com.example.allaroundapp.databinding.FragmentIndividualChatBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private lateinit var chatAdapter: IndividualChatAdapter

class IndividualChatFragment: Fragment() {
    private var _binding: FragmentIndividualChatBinding? = null
    private val binding get() = _binding!!

    private val args: IndividualChatFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()

    private var updateMessagesAdapterJob: Job? = null

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
        setupRecyclerView()
        subscribeToUiUpdates()
        listenToSocketEvents()
        connectToChat(args.username, args.chatPartner)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar).visibility =
            View.GONE

        binding.btnSendMessage.setOnClickListener {
            viewModel.sendChatMessage(
                args.username,
                args.chatPartner,
                binding.etChatMessage.text.toString()
            )
            binding.etChatMessage.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeToUiUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.individualMessages.collect { messages ->
                    val canScrollDown = binding.rvChatMessages.canScrollVertically(1)
                    updateMessagesList(messages)
                    updateMessagesAdapterJob?.join()
                    if(!canScrollDown) {
                        binding.rvChatMessages.scrollToPosition(chatAdapter.chatMessages.size - 1)
                    }
                }
            }
        }
    }

    private fun listenToSocketEvents() {
       viewLifecycleOwner.lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.socketEvents.collect { baseModel ->
                    when(baseModel) {
                        is ConnectedToSocket -> {
                            connectToChat(args.username, args.chatPartner)
                        }
                    }
                }
           }
       }
    }

    private fun connectToChat(username: String, chatPartner: String) {
        binding.tvChatPartner.text = args.chatPartner
        viewModel.sendJoinChatRequest(username, chatPartner)
    }

    private fun setupRecyclerView() {
        chatAdapter = IndividualChatAdapter(args.username)
        binding.rvChatMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateMessagesList(messages: List<NormalChatMessage>) {
        updateMessagesAdapterJob?.cancel()
        updateMessagesAdapterJob = lifecycleScope.launch {
            chatAdapter.updateDataset(messages)
        }
    }
}