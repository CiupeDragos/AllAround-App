package com.example.allaroundapp.ui.fragments.individualchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.IndividualChatAdapter
import com.example.allaroundapp.data.models.ConnectedToSocket
import com.example.allaroundapp.data.models.NormalChatMessage
import com.example.allaroundapp.databinding.FragmentIndividualChatBinding
import com.example.allaroundapp.other.Constants
import com.example.allaroundapp.other.Constants.MESSAGE_DAY_ANNOUNCEMENT
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class IndividualChatFragment : Fragment() {
    private var _binding: FragmentIndividualChatBinding? = null
    private val binding get() = _binding!!

    private val args: IndividualChatFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()

    private var updateMessagesAdapterJob: Job? = null

    private var currentMessageDayJob: Job? = null

    private lateinit var chatAdapter: IndividualChatAdapter

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
                    println(messages)
                    val timedMessages = addTimestampsForMessages(messages.toMutableList())
                    println(timedMessages)
                    updateMessagesList(timedMessages)
                    updateMessagesAdapterJob?.join()
                    if (!canScrollDown) {
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
                    when (baseModel) {
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
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
        val messages = chatAdapter.chatMessages

        if (messages[index].sender != MESSAGE_DAY_ANNOUNCEMENT) {
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

    private fun updateMessagesList(messages: List<NormalChatMessage>) {
        updateMessagesAdapterJob?.cancel()
        updateMessagesAdapterJob = lifecycleScope.launch {
            chatAdapter.updateDataset(messages)
        }
    }

    private fun addTimestampsForMessages(messages: MutableList<NormalChatMessage>): List<NormalChatMessage> {
        var todayHandled = false
        var i = 0
        var counter = messages.size
        while (i < counter) {
            if (i == 0) {
                var messageTime = viewModel.formatMessageTimestamp(
                    System.currentTimeMillis(),
                    messages[0].timestamp
                )
                println("Message time: $messageTime")
                if(messageTime == "NO_HANDLE") messageTime = "Today"
                val messageForTime = NormalChatMessage(
                    message = messageTime,
                    sender = MESSAGE_DAY_ANNOUNCEMENT,
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
                        val messageForTime = NormalChatMessage(
                            message = messageTime,
                            sender = MESSAGE_DAY_ANNOUNCEMENT,
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
                        val messageForTime = NormalChatMessage(
                            message = messageTime,
                            sender = MESSAGE_DAY_ANNOUNCEMENT,
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