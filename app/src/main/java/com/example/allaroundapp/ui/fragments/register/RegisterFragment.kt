package com.example.allaroundapp.ui.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.allaroundapp.R
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.databinding.FragmentRegisterBinding
import com.example.allaroundapp.other.customSnackbar
import com.example.allaroundapp.other.hideKeyboard
import com.example.allaroundapp.other.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToRegisterEvent()

        progressBar = requireActivity().findViewById(R.id.progressBar)

        binding.btnRegister.setOnClickListener {
            registerAccount()
        }

        binding.btnBackToLogin.setOnClickListener {
            backToLogin()
        }
    }

    private fun listenToRegisterEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.registerEvent.collect { event ->
                hideProgressBar()
                when(event) {
                    is RegisterViewModel.RegisterError.EmptyFieldError -> {
                        customSnackbar(R.string.register_field_empty_error)
                    }
                    is RegisterViewModel.RegisterError.UsernameTooShortError -> {
                        customSnackbar(R.string.username_too_short_error)
                    }
                    is RegisterViewModel.RegisterError.UsernameTooLongError -> {
                        customSnackbar(R.string.username_too_long_error)
                    }
                    is RegisterViewModel.RegisterError.PasswordTooShortError -> {
                        customSnackbar(R.string.password_too_short_error)
                    }
                    is RegisterViewModel.RegisterError.PasswordTooLongError -> {
                        customSnackbar(R.string.password_too_long_error)
                    }
                    is RegisterViewModel.RegisterError.PasswordsNotTheSameError -> {
                        customSnackbar(R.string.passwords_do_not_match)
                    }
                    is RegisterViewModel.RegisterError.RegisterAccountError -> {
                        customSnackbar(event.error)
                    }
                    is RegisterViewModel.RegisterError.RegisterAccountSuccess -> {
                        customSnackbar(event.message)

                        requireActivity().hideKeyboard(binding.root)

                        binding.etUsername.text.clear()
                        binding.etPassword.text.clear()
                        binding.etRepeatPassword.text.clear()
                    }
                }
            }
        }
    }

    private fun backToLogin() {
        binding.etUsername.text.clear()
        binding.etPassword.text.clear()
        binding.etRepeatPassword.text.clear()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigateSafely(
            R.id.action_registerFragment_to_loginFragment,
            navOptions = navOptions
        )
    }

    private fun registerAccount() {
        progressBar.visibility = View.VISIBLE

        viewModel.validateCredentialsAndCreateAccount(
            binding.etUsername.text.toString(),
            binding.etPassword.text.toString(),
            binding.etRepeatPassword.text.toString()
        )
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}