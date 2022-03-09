package com.example.allaroundapp.ui.fragments.chats

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.RecentChatsAdapter
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.data.models.RecentChat
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.other.Constants.KEY_LOGIN_USERNAME
import com.example.allaroundapp.other.Constants.NO_USERNAME
import com.example.allaroundapp.other.navigateSafely
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var recentChatsAdapter: RecentChatsAdapter

    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var loggedInUsername: String

    private var recentChatsUpdateJob: Job? = null

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loggedInUsername = sharedPref.getString(KEY_LOGIN_USERNAME, NO_USERNAME) ?: NO_USERNAME
        progressBar = requireActivity().findViewById(R.id.progressBar)
        setupRecyclerView()
        observeConnectionEvents()
        observeSocketEvents()
        subscribeToUiUpdates()
        getMyRecentChats()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar).visibility =
            View.VISIBLE

        recentChatsAdapter.setOnChatClickListener { clickedChat ->
            val bundle = Bundle().apply {
                putString("username", loggedInUsername)
                putString("chatPartner", clickedChat.chattingTo)
            }
            findNavController().navigateSafely(
                R.id.action_chatsFragment_to_individualChatFragment,
                args = bundle
            )
        }

        recentChatsAdapter.setOnGroupClickListener { clickedGroup ->
            val bundle = Bundle().apply {
                putString("username", loggedInUsername)
                putString("groupId", clickedGroup.groupId)
                putString("groupName", clickedGroup.name)
            }
            findNavController().navigateSafely(
                R.id.action_chatsFragment_to_groupChatFragment,
                args = bundle
            )
        }

        binding.createGroupCard.setOnClickListener {
            val bundle = Bundle().apply {
                putString("username", loggedInUsername)
            }
            findNavController().navigateSafely(
                R.id.action_chatsFragment_to_createGroupFragment,
                args = bundle
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeToUiUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recentMessages.collect { recentMessages ->
                    //hideProgressBar()
                    if(recentMessages.groups.isNotEmpty()) {
                        Log.d("Groups", recentMessages.groups[0].lastMessageSender)
                    }
                    val chatsList = (recentMessages.chats + recentMessages.groups)
                        .sortedByDescending { it.lastMessageTimestamp }
                    updateRecentChats(chatsList)
                }
            }
        }
    }

    private fun observeSocketEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.socketEvents.collect { baseModel ->
                when(baseModel) {
                    is ConnectedToSocket -> {
                        getMyRecentChats()
                    }
                }
            }
        }
    }

    private fun observeConnectionEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.connectionEvents.collect { event ->
                when (event) {
                    is WebSocket.Event.OnConnectionOpened<*> -> {
                        //Log.d("Recent chats", "On connection opened recent chats called")
                        //getMyRecentChats()
                    }
                    is WebSocket.Event.OnConnectionClosed -> {
                        Log.d("Recent chats", "On connection closed")
                    }
                }
            }
        }
    }

    private fun getMyRecentChats() {
        Log.d("Recent chats", "Recent chats called")
        //progressBar.visibility = View.VISIBLE
        viewModel.sendJoinMyChatsRequest(loggedInUsername)
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        recentChatsAdapter = RecentChatsAdapter(loggedInUsername, requireContext(), viewModel)
        binding.rvRecentChats.apply {
            adapter = recentChatsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun updateRecentChats(chatList: List<RecentChat>) {
        recentChatsUpdateJob?.cancel()
        recentChatsUpdateJob = lifecycleScope.launch {
            recentChatsAdapter.updateDataset(chatList)
        }
    }
}