package ru.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentSignUpBinding
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewModel.SignUpViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setupListeners()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupListeners() {
        binding.apply {
            signInButton.setOnClickListener {
                findNavController().navigate(R.id.signUpFragmentToSignInFragment)
            }
            nextStepButton.setOnClickListener {
                if (checkData()) {
                    viewModel.saveLoginAndPassword(login.text.toString(), password.text.toString())
                    findNavController().navigate(R.id.signUpFragmentToPersonalDataFragment)
                }
                AndroidUtils.hideKeyboard(it)
            }
            backButton.setOnClickListener{
                findNavController().navigate(R.id.signUpFragmentToWelcomeFragment)
            }
        }
    }

    private fun checkData(): Boolean {
        binding.apply {
            return if (login.text.isNullOrEmpty() ||
                password.text.isNullOrEmpty() ||
                repeatPassword.text.isNullOrEmpty()
            ) {
                Toast.makeText(requireActivity(), R.string.input_fields_empty, Toast.LENGTH_SHORT)
                    .show()
                false
            } else {
                if (password.text.toString() == repeatPassword.text.toString()) {
                    true
                } else {
                    Toast.makeText(requireActivity(), R.string.passwords_didnt_match, Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            }
        }
    }


}