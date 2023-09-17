package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.login.setOnClickListener {
            val accountName = binding.username.text.toString()
            val accountPassword = binding.password.text.toString()

            if (accountName.isBlank() || accountPassword.isBlank()) {
                AndroidUtils.hideKeyboard(requireView())
                Snackbar.make(binding.root, R.string.noLoginData, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            loginViewModel.tryLogin(username = accountName, password = accountPassword)
            authViewModel.state.observe(viewLifecycleOwner) {
                if (authViewModel.authorized) {
                    findNavController().navigateUp()
                }
            }
        }

        return binding.root
    }
}