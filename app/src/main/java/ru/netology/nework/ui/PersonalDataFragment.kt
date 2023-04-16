package ru.netology.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPersonalDataBinding
import ru.netology.nework.viewModel.MediaViewModel
import ru.netology.nework.viewModel.SignUpViewModel

class PersonalDataFragment : Fragment() {
    private lateinit var binding : FragmentPersonalDataBinding
    private val viewModel : SignUpViewModel by activityViewModels()
    private val mediaViewModel : MediaViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        mediaViewModel.onPhotoPickerSelect(it)
    }

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(
                    requireContext(),
                    R.string.error_photo,
                    Toast.LENGTH_SHORT
                ).show()
            }
            Activity.RESULT_OK -> {
                val uri : Uri? = it.data?.data
                if (uri != null) {
                    mediaViewModel.onPhotoPickerSelect(uri)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun initBinding (inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentPersonalDataBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupListeners() {
        binding.apply {
            addPhoto.setOnClickListener {
                ImagePicker.Builder(requireActivity())
                    .crop()
                    .cameraOnly()
                    .maxResultSize(2048, 2048)
                    .createIntent(imageLauncher::launch)
            }
            insertPhoto.setOnClickListener {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            deleteAvatar.setOnClickListener {
                mediaViewModel.deleteMedia()
            }
            createAccount.setOnClickListener {
                if (checkData()) {
                    viewModel.saveName(name.text.toString())
                }
            }

        }
    }

    private fun checkData() : Boolean {
        binding.apply {
            return if (name.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), R.string.input_fields_empty, Toast.LENGTH_SHORT)
                    .show()
                false
            } else {
                true
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
                    mediaViewModel.deleteMedia()
                    findNavController().navigate(R.id.personalDataFragmentToPlacesOfWorkFragment)
                }
            }
        }
        mediaViewModel.photo.observe(viewLifecycleOwner) {
            binding.apply {
                if (it != null) {
                    avatarContainer.visibility = View.VISIBLE
                    avatarButtonContainer.visibility = View.GONE
                    avatar.setImageURI(it.uri)
                } else {
                    avatarContainer.visibility = View.GONE
                    avatarButtonContainer.visibility = View.VISIBLE
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
            addPhoto.isEnabled = true
            insertPhoto.isEnabled = true
            createAccount.isEnabled = true
            deleteAvatar.isEnabled = true
            name.isEnabled = true
        }
    }

    private fun disable() {
        binding.apply {
            addPhoto.isEnabled = false
            insertPhoto.isEnabled = false
            createAccount.isEnabled = false
            deleteAvatar.isEnabled = false
            name.isEnabled = false
        }
    }
}