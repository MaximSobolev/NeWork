package ru.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentSignInBinding
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewModel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setupObservers()
        setupListeners()
        return binding.root
    }


    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupListeners() {
        binding.apply {
            signInButton.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                if (login.text.isNullOrEmpty() || password.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.input_fields_empty,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    viewModel.signIn(login.text.toString(), password.text.toString())
                }
            }
            signUpButton.setOnClickListener {
                findNavController().navigate(R.id.signInFragmentToSignUpFragment)
            }
            backButton.setOnClickListener {
                findNavController().navigate(R.id.signInFragmentToWelcomeFragment)
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            binding.apply {
                progressBar.isVisible = it.loading
                if (it.loading) {
                    disable()
                } else {
                    enable()
                }
                if (it.idle) {
                    findNavController().navigate(R.id.signInFragmentToPostsWallFragment)
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getText(it), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun enable() {
        binding.apply {
            signInButton.isEnabled = true
            signUpButton.isEnabled = true
            login.isEnabled = true
            password.isEnabled = true
        }
    }

    private fun disable() {
        binding.apply {
            signInButton.isEnabled = false
            signUpButton.isEnabled = false
            login.isEnabled = false
            password.isEnabled = false
        }
    }

}