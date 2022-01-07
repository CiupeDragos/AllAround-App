package com.example.allaroundapp.ui.fragments.creategroup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allaroundapp.R
import com.example.allaroundapp.adapters.SelectedGroupMember
import com.example.allaroundapp.adapters.MembersToSelectAdapter
import com.example.allaroundapp.adapters.SelectedMembersAdapter
import com.example.allaroundapp.databinding.FragmentCreateGroupBinding
import com.example.allaroundapp.other.Constants.MAX_CHAT_GROUP_NAME_LENGTH
import com.example.allaroundapp.other.Constants.MIN_CHAT_GROUP_NAME_LENGTH
import com.example.allaroundapp.other.customSnackbar
import com.example.allaroundapp.other.navigateSafely
import com.example.allaroundapp.ui.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var membersToSelectAdapter: MembersToSelectAdapter
    private lateinit var selectedMembersAdapter: SelectedMembersAdapter

    private val args: CreateGroupFragmentArgs by navArgs()

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: CreateGroupViewModel by viewModels()

    private var searchUsersJob: Job? = null

    private lateinit var friends: List<SelectedGroupMember>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        subscribeToUiUpdates()
        disconnectUser()
        getFriends()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
            .isVisible = false
        Log.d("Lifecycle", "OnViewCreated called")

        membersToSelectAdapter.setOnMemberClick { member ->
            val curList = membersToSelectAdapter.members.toMutableList()
            checkMember(curList, member)
        }

        selectedMembersAdapter.setOnSelectedMemberClick { memberUsername ->
            uncheckMember(memberUsername)
        }

        binding.etSearchPeople.addTextChangedListener {
            val text = it.toString()
            searchUsersJob?.cancel()
            if(text.isNotEmpty()) {
                searchUsersJob = lifecycleScope.launch {
                    delay(500L)
                    binding.tvNoUsersFound.isVisible = false
                    binding.searchUsersProgressBar.isVisible = true
                    viewModel.findUsers(text)
                }
            } else {
                binding.tvNoUsersFound.isVisible = false
                membersToSelectAdapter.members = friends.apply {
                    for (i in this.indices) {
                        this[i].isSelected = this[i].username in selectedMembersAdapter.differ.currentList
                    }
                }
                membersToSelectAdapter.notifyDataSetChanged()
            }
        }

        binding.btnCreateGroup.setOnClickListener {
            viewModel.validateNameAndCreateGroup(
                binding.etGroupName.text.toString(),
                selectedMembersAdapter.differ.currentList + args.username
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun subscribeToUiUpdates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apiEvents.collect { event ->
                    when (event) {
                        is CreateGroupViewModel.GroupEvents.GetFriendsApiCall -> {
                            if (event.friends.isNotEmpty()) {
                                membersToSelectAdapter.members =
                                    event.friends.map { friendUsername ->
                                        SelectedGroupMember(friendUsername, false)
                                    }
                                friends = event.friends.map { friendUsername ->
                                    SelectedGroupMember(friendUsername, false)
                                }
                            } else {
                                friends = listOf()
                            }
                            membersToSelectAdapter.notifyDataSetChanged()
                        }
                        is CreateGroupViewModel.GroupEvents.GetUsersApiCall -> {
                            binding.searchUsersProgressBar.isVisible = false
                            val alreadySelectedUsers = selectedMembersAdapter.differ.currentList
                            membersToSelectAdapter.members = event.users.map { username ->
                                if(username in alreadySelectedUsers) {
                                    SelectedGroupMember(username, true)
                                } else {
                                    SelectedGroupMember(username, false)
                                }
                            }
                            if(event.users.isEmpty()) {
                                binding.tvNoUsersFound.isVisible = true
                            }
                            membersToSelectAdapter.notifyDataSetChanged()
                        }
                        is CreateGroupViewModel.GroupEvents.GroupNameLengthError -> {
                            customSnackbar(getString(
                                R.string.group_name_error,
                                MIN_CHAT_GROUP_NAME_LENGTH,
                                MAX_CHAT_GROUP_NAME_LENGTH
                            ))
                        }
                        is CreateGroupViewModel.GroupEvents.CreateGroupError -> {
                            customSnackbar(event.error)
                        }
                        is CreateGroupViewModel.GroupEvents.CreateGroupSuccess -> {
                            customSnackbar("Chat group created successfully")
                            val groupDetails = event.message.split(",")
                            val groupId = groupDetails[0]
                            val groupName = groupDetails[1]
                            openGroupChatWindows(args.username, groupId, groupName)
                        }
                    }
                }
            }
        }
    }

    private fun openGroupChatWindows(username: String, groupId: String, groupName: String) {
        val bundle = Bundle().apply {
            putString("username", username)
            putString("groupId", groupId)
            putString("groupName", groupName)
        }
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.createGroupFragment, true)
            .build()

        findNavController().navigateSafely(
            R.id.action_createGroupFragment_to_groupChatFragment,
            args = bundle,
            navOptions = navOptions
        )
    }

    private fun setupRecyclerViews() {
        membersToSelectAdapter = MembersToSelectAdapter()
        selectedMembersAdapter = SelectedMembersAdapter()
        Log.d("RecycleSetup", "New adapters initialized")
        binding.apply {
            rvSearchPeople.adapter = membersToSelectAdapter
            rvSearchPeople.layoutManager = LinearLayoutManager(requireContext())

            rvSelectedMembers.adapter = selectedMembersAdapter
            rvSelectedMembers.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun checkMember(
        curList: MutableList<SelectedGroupMember>,
        member: SelectedGroupMember
    ) {
        val index = curList.indexOf(member)
        curList[index] = member.apply {
            val selectedMembersList = selectedMembersAdapter.differ.currentList.toMutableList()
            isSelected = !isSelected
            if (isSelected) {
                if (selectedMembersList.isEmpty()) {
                    binding.tvNoMembers.isVisible = false
                }
                selectedMembersList.add(member.username)
                selectedMembersAdapter.differ.submitList(selectedMembersList.toList())
            } else {
                selectedMembersList.remove(member.username)
                selectedMembersAdapter.differ.submitList(selectedMembersList.toList())
                if (selectedMembersList.isEmpty()) {
                    binding.tvNoMembers.isVisible = true
                }
            }
        }
        membersToSelectAdapter.members = curList.toList()
        membersToSelectAdapter.notifyItemChanged(index)
    }

    private fun uncheckMember(username: String) {
        val curList = selectedMembersAdapter.differ.currentList.toMutableList()
        val membersToSelectList = membersToSelectAdapter.members.toMutableList()
        var index = -1
        curList.remove(username)
        if(curList.isEmpty()) {
            binding.tvNoMembers.isVisible = true
        }
        for (i in membersToSelectList.indices) {
            if (membersToSelectList[i].username == username) {
                index = i
                membersToSelectList[i].isSelected = false
                break
            }
        }
        if(index != -1) {
            membersToSelectAdapter.members = membersToSelectList.toList()
            membersToSelectAdapter.notifyItemChanged(index)
        }

        selectedMembersAdapter.differ.submitList(curList.toList())
    }

    private fun disconnectUser() {
        mainViewModel.disconnectUnchattingUser(args.username)
    }

    private fun getFriends() {
        viewModel.findFriends()
    }
}