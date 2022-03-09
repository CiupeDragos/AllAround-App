package com.example.allaroundapp.ui.fragments.groupchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.GroupChatAdapter
import com.example.allaroundapp.data.models.ChatGroupMessage
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.databinding.FragmentGroupChatBinding
import com.example.allaroundapp.other.Constants
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment: Fragment() {
    private var _binding: com.example.allaroundapp.databinding.FragmentGroupChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val args: GroupChatFragmentArgs by navArgs()

    private var updateGroupChatMessagesJob: Job? = null
    private var currentMessageDayJob: Job? = null

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
        popCreateGroupFragment()
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
                    val timed_messages = addTimestampsForMessages(messages.toMutableList())
                    updateGroupMessages(timed_messages)
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

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when(newState) {
                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            currentMessageDayJob?.cancel()
                            binding.messageDayLayout.visibility = View.VISIBLE
                        }
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            currentMessageDayJob = lifecycleScope.launch {
                                delay(500L)
                                binding.messageDayLayout.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager

                    when {
                        dy > 0 -> {
                            val index = llm.findLastCompletelyVisibleItemPosition()
                            handleMessageDate(index)
                        }
                        dy < 0 -> {
                            val index = llm.findFirstCompletelyVisibleItemPosition()
                            handleMessageDate(index)
                        }
                    }
                }
            })
        }
    }

    private fun handleMessageDate(index: Int) {
        val messages = groupAdapter.groupChatMessages

        if (messages[index].sender != Constants.MESSAGE_DAY_ANNOUNCEMENT) {
            val messageTimestamp = messages[index].timestamp
            var messageDate = viewModel.formatMessageTimestamp(
                System.currentTimeMillis(),
                messageTimestamp
            )
            if(messageDate == "NO_HANDLE") {
                messageDate = "Today"
            }
            binding.messageDayText.text = messageDate
        } else {
            binding.messageDayText.text = messages[index].message
        }
    }

    private fun connectToGroupChat() {
        binding.tvChatPartner.text = args.groupName
        viewModel.sendJoinGroupRequest(args.username, args.groupId)
    }

    private fun popCreateGroupFragment() {
        requireActivity()
            .supportFragmentManager
            .popBackStack("createGroupFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun addTimestampsForMessages(messages: MutableList<ChatGroupMessage>): List<ChatGroupMessage> {
        var todayHandled = false
        var i = 0
        var counter = messages.size
        while (i < counter) {
            if (i == 0) {
                var messageTime = viewModel.formatMessageTimestamp(
                    System.currentTimeMillis(),
                    messages[0].timestamp
                )
                if(messageTime == "NO_HANDLE") messageTime = "Today"
                val messageForTime = ChatGroupMessage(
                    message = messageTime,
                    sender = Constants.MESSAGE_DAY_ANNOUNCEMENT,
                    "",
                    0
                )
                messages.add(0, messageForTime)
                counter++
                i += 2
            } else {
                val messageTime = viewModel.formatMessageTimestamp(
                    messages[i - 1].timestamp,
                    messages[i].timestamp
                )
                if (messageTime == "Today") {
                    if (!todayHandled) {
                        val messageForTime = ChatGroupMessage(
                            message = messageTime,
                            sender = Constants.MESSAGE_DAY_ANNOUNCEMENT,
                            "",
                            0
                        )
                        messages.add(i, messageForTime)
                        counter++
                        i += 2
                        todayHandled = true
                    } else {
                        i++
                    }
                } else {
                    if (messageTime != "NO_HANDLE") {
                        val messageForTime = ChatGroupMessage(
                            message = messageTime,
                            sender = Constants.MESSAGE_DAY_ANNOUNCEMENT,
                            "",
                            0
                        )
                        messages.add(i, messageForTime)
                        i += 2
                        counter++
                    } else {
                        i++
                    }
                }
            }
        }
        return messages.toList()
    }
}