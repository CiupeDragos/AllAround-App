package com.example.allaroundapp.ui.fragments.login

import android.content.SharedPreferences
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
import com.example.allaroundapp.data.BasicAuthInterceptor
import com.example.allaroundapp.databinding.FragmentLoginBinding
import com.example.allaroundapp.other.Constants.KEY_LOGIN_USERNAME
import com.example.allaroundapp.other.Constants.KEY_PASSWORD
import com.example.allaroundapp.other.Constants.NO_PASSWORD
import com.example.allaroundapp.other.Constants.NO_USERNAME
import com.example.allaroundapp.other.customSnackbar
import com.example.allaroundapp.other.navigateSafely
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private val viewModel: LoginViewModel by viewModels()

    private var curUsername: String? = null
    private var curPassword: String? = null

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToLoginEvents()

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        progressBar = requireActivity().findViewById(R.id.progressBar)
        bottomNav.visibility = View.GONE
        if(isLoggedIn()) {
            redirectLogin()
        }

        binding.tvCreateAccount.setOnClickListener {
            binding.etUsername.text.clear()
            binding.etPassword.text.clear()
            findNavController().navigateSafely(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            curUsername = username
            curPassword = password

            progressBar.visibility = View.VISIBLE
            viewModel.validateCredentialsAndLogin(username, password)
        }
    }

    private fun listenToLoginEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->
                hideProgressBar()
                when(event) {
                    is LoginViewModel.LoginEvent.FieldEmptyError -> {
                        customSnackbar(R.string.login_field_empty_error)
                    }
                    is LoginViewModel.LoginEvent.LoginError -> {
                        customSnackbar(event.error)
                    }
                    is LoginViewModel.LoginEvent.LoginSuccess -> {
                        sharedPref.edit().putString(KEY_LOGIN_USERNAME, curUsername).apply()
                        sharedPref.edit().putString(KEY_PASSWORD, curPassword).apply()
                        basicAuthInterceptor.username = curUsername
                        customSnackbar(event.data)
                        redirectLogin()
                    }
                }
            }
        }
    }

    private fun redirectLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigateSafely(
            R.id.action_loginFragment_to_chatsFragment,
            navOptions = navOptions
        )
    }

    private fun isLoggedIn(): Boolean {
        val username = sharedPref.getString(KEY_LOGIN_USERNAME, NO_USERNAME)
        val password = sharedPref.getString(KEY_PASSWORD, NO_PASSWORD)

        return username != NO_USERNAME && password != NO_PASSWORD
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}