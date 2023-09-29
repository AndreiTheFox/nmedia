package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authViewModel by viewModels<AuthViewModel>()
        val binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )
        val loginViewModel: LoginViewModel by viewModels(
            ownerProducer = ::requireParentFragment
        )

        binding.navBack.setOnClickListener {
            findNavController().navigateUp()
        }
        loginViewModel.dataState.observe(viewLifecycleOwner) { state->
            when {
                state.userNotFoundError || state.incorrectPasswordError -> Toast.makeText(context, R.string.user_not_found.toString(), Toast.LENGTH_LONG).show()
                state.error -> Toast.makeText(context, R.string.unknown_error.toString(), Toast.LENGTH_LONG).show()
            }

        }
        authViewModel.state.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
                findNavController().navigateUp()
            }

            binding.login.setOnClickListener {
                AndroidUtils.hideKeyboard(requireView())
                val accountName = binding.userLogin.text.toString()
                val accountPassword = binding.password.text.toString()
                if (accountName.isBlank() || accountPassword.isBlank()) {
                    Snackbar.make(binding.root, R.string.noLoginData, Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                loginViewModel.tryLogin(username = accountName, password = accountPassword)
            }
        }

        return binding.root
    }
}