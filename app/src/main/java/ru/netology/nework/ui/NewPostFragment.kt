package ru.netology.nework.ui

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dialogFragment.AddMentionUser
import ru.netology.nework.dialogFragment.ChooseAudioFileFragment
import ru.netology.nework.dialogFragment.ChooseTypeFileFragment
import ru.netology.nework.dto.AttachType
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.recyclerView.mentionUserInNewPostFragment.SelectedUsersNewEntityAdapter
import ru.netology.nework.recyclerView.mentionUserInNewPostFragment.SelectedUsersNewEntityListener
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewModel.MediaViewModel
import ru.netology.nework.viewModel.PermissionViewModel
import ru.netology.nework.viewModel.PostViewModel
import ru.netology.nework.viewModel.UserViewModel

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    private lateinit var adapter: SelectedUsersNewEntityAdapter

    private val userViewModel: UserViewModel by activityViewModels()
    private val mediaViewModel : MediaViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private val args: NewPostFragmentArgs by navArgs()

    private var editPost: Post? = null

    private val retriever = MediaMetadataRetriever()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestAllUsers()
        initBinding(inflater, container)
        setupAdapter()
        setupVisibility()
        setupListeners()
        setupObservers()
        setupData()
        return binding.root
    }

    private fun requestAllUsers() {
        userViewModel.requestUsers()
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupAdapter() {
        adapter = SelectedUsersNewEntityAdapter(object : SelectedUsersNewEntityListener {
            override fun deleteSelectedUser(userid: Int) {
                userViewModel.deleteSelectedUser(userid)
            }
        })
        binding.mentionedUserList.adapter = adapter
    }

    private fun setupVisibility() {
        binding.apply {
            linkGroup.visibility = View.GONE
            deleteMedia.visibility = View.GONE
            photoContent.visibility = View.GONE
            videoGroup.visibility = View.GONE
            audioGroup.visibility = View.GONE
            mentionedUserGroup.visibility = View.GONE
        }
    }

    private fun setupData() {
        editPost = args.editPost ?: return
        binding.apply {
            editPost?.let { post ->
                messageText.setText(post.content)
                if (post.link != null) {
                    linkGroup.visibility = View.VISIBLE
                    linkText.setText(editPost?.link)
                    addLink.visibility = View.GONE
                }
                mediaViewModel.setAttachment(post.attachment)
                userViewModel.setupUsersFromSelectedIds(post.mentionIds)
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            backButton.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                mediaViewModel.deleteMedia()
                userViewModel.clear()
                findNavController().navigateUp()
            }
            addLink.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                linkGroup.visibility = View.VISIBLE
                addLink.visibility = View.GONE
            }
            deleteLink.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                linkText.text?.clear()
                linkGroup.visibility = View.GONE
                addLink.visibility = View.VISIBLE
            }
            addAttachment.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                ChooseTypeFileFragment(mediaViewModel, permissionViewModel).show(
                    parentFragmentManager,
                    "dialog"
                )
            }
            addMentionUser.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                AddMentionUser(userViewModel).show(parentFragmentManager, "dialog")
            }
            addPost.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                sendPost()
            }
            deleteMedia.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                mediaViewModel.deleteMedia()
            }
        }
    }

    private fun sendPost() {
        binding.apply {
            if (messageText.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), R.string.content_empty, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (editPost != null) {
                    editPost?.let { post ->
                        postViewModel.saveId(post.id)
                    }
                }
                val message = messageText.text.toString()
                postViewModel.saveMessage(message)
                val link = linkText.text.toString().ifEmpty { null }
                postViewModel.saveLink(link)
                postViewModel.saveMentionIds(userViewModel.getSelectedUserIds())
                postViewModel.savePost()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupObservers() {
        userViewModel.selectedUsers.observe(viewLifecycleOwner) { userList ->
            when (userList) {
                emptyList<User>(), null -> {
                    binding.mentionedUserGroup.visibility = View.GONE
                }
                else -> {
                    binding.mentionedUserGroup.visibility = View.VISIBLE
                    adapter.submitList(userList)
                }
            }
        }
        userViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(it), Toast.LENGTH_SHORT).show()
        }
        mediaViewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo != null) {
                binding.apply {
                    photoContent.visibility = View.VISIBLE
                    deleteMedia.visibility = View.VISIBLE
                    photoContent.setImageURI(photo.uri)
                    addAttachment.visibility = View.GONE
                }
            } else {
                binding.apply {
                    photoContent.visibility = View.GONE
                    deleteMedia.visibility = View.GONE
                    addAttachment.visibility = View.VISIBLE
                }
            }
        }
        mediaViewModel.video.observe(viewLifecycleOwner) { video ->
            if (video != null) {
                binding.apply {
                    retriever.setDataSource(video.file.absolutePath)
                    val preview = retriever.getFrameAtTime(0)
                    preview.let {
                        videoPreview.setImageBitmap(it)
                    }
                    videoGroup.visibility = View.VISIBLE
                    deleteMedia.visibility = View.VISIBLE
                    addAttachment.visibility = View.GONE
                }

            } else {
                binding.apply {
                    videoGroup.visibility = View.GONE
                    deleteMedia.visibility = View.GONE
                    addAttachment.visibility = View.VISIBLE
                }
            }
        }
        mediaViewModel.audioContent.observe(viewLifecycleOwner) { audio ->
            if (audio != null) {
                binding.apply {
                    audioName.text = audio.name
                    audioGroup.visibility = View.VISIBLE
                    deleteMedia.visibility = View.VISIBLE
                    addAttachment.visibility = View.GONE
                }
            } else {
                binding.apply {
                    audioGroup.visibility = View.GONE
                    deleteMedia.visibility = View.GONE
                    addAttachment.visibility = View.VISIBLE
                }
            }
        }
        mediaViewModel.audioFiles.observe(viewLifecycleOwner) { audioList ->
            audioList?.let { list ->
                if (list.isNotEmpty()) {
                    ChooseAudioFileFragment(mediaViewModel).show(parentFragmentManager, "dialog")
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.audio_files_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        mediaViewModel.attach.observe(viewLifecycleOwner) { attach ->
            binding.apply {
                if (attach != null) {
                    when (attach.type) {
                        AttachType.IMAGE -> {
                            Glide.with(photoContent)
                                .load(attach.url)
                                .placeholder(R.drawable.outline_account_circle_24)
                                .error(R.drawable.outline_error_outline_24)
                                .timeout(10_000)
                                .into(photoContent)
                            photoContent.visibility = View.VISIBLE
                        }
                        AttachType.VIDEO -> {
                            Glide.with(videoPreview)
                                .load(attach.url)
                                .placeholder(R.drawable.outline_account_circle_24)
                                .error(R.drawable.outline_error_outline_24)
                                .timeout(10_000)
                                .into(videoPreview)
                            videoGroup.visibility = View.VISIBLE
                        }
                        AttachType.AUDIO -> {
                            audioName.text = getString(R.string.audio_file)
                            audioGroup.visibility = View.VISIBLE
                        }
                    }
                    deleteMedia.visibility = View.VISIBLE
                    addAttachment.visibility = View.GONE
                } else {
                    photoContent.visibility = View.GONE
                    videoGroup.visibility = View.GONE
                    audioGroup.visibility = View.GONE
                    deleteMedia.visibility = View.GONE
                    addAttachment.visibility = View.VISIBLE
                }
            }
        }
    }
}