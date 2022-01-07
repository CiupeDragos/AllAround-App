package com.example.allaroundapp.ui.fragments.friends

import android.content.SharedPreferences
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.FriendsAdapter
import com.example.allaroundapp.databinding.FragmentChatsBinding
import com.example.allaroundapp.databinding.FragmentFriendsBinding
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.other.Constants.KEY_LOGIN_USERNAME
import com.example.allaroundapp.other.Constants.NO_USERNAME
import com.example.allaroundapp.other.navigateSafely
import com.example.allaroundapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FriendsFragment: Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val friendsViewModel: FriendsViewModel by viewModels()

    private lateinit var loggedInUsername: String

    private lateinit var friendsAdapter: FriendsAdapter

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLoggedInUsername()
        disconnectUser()
        setupRecyclerView()
        subscribeToUiUpdates()
        getFriends()

        binding.tvSentFriendRequests.setOnClickListener {
            val bundle = Bundle().apply {
                putString("username", loggedInUsername)
            }
            findNavController().navigateSafely(
                R.id.action_friendsFragment_to_sentRequestsFragment,
                args = bundle
            )
        }

        binding.tvReceivedFriendRequests.setOnClickListener {
            val bundle = Bundle().apply {
                putString("username", loggedInUsername)
            }
            findNavController().navigateSafely(
                R.id.action_friendsFragment_to_receivedRequestFragment,
                args = bundle
            )
        }

        binding.sendRequestCard.setOnClickListener {
            findNavController().navigateSafely(
                R.id.action_friendsFragment_to_sendRequestsFragment
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getLoggedInUsername() {
        loggedInUsername = sharedPref.getString(KEY_LOGIN_USERNAME, NO_USERNAME) ?: NO_USERNAME
    }

    private fun disconnectUser() {
        viewModel.disconnectUnchattingUser(loggedInUsername)
    }

    private fun getFriends() {
        friendsViewModel.getFriends()
    }

    private fun subscribeToUiUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                friendsViewModel.apiEvents.collect { list ->
                    if(list.isNotEmpty()) {
                        friendsAdapter.differ.submitList(list)
                        binding.tvNoFriends.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        friendsAdapter = FriendsAdapter()
        binding.rvFriends.apply {
            adapter = friendsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}