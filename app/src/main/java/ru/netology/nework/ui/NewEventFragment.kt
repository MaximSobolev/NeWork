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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewEventBinding
import ru.netology.nework.dialogFragment.AddMentionUser
import ru.netology.nework.dialogFragment.ChooseAudioFileFragment
import ru.netology.nework.dialogFragment.ChooseTypeFileFragment
import ru.netology.nework.dto.AttachType
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.User
import ru.netology.nework.recyclerView.mentionUserInNewPostFragment.SelectedUsersNewEntityAdapter
import ru.netology.nework.recyclerView.mentionUserInNewPostFragment.SelectedUsersNewEntityListener
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.AppUtils
import ru.netology.nework.viewModel.EventViewModel
import ru.netology.nework.viewModel.MediaViewModel
import ru.netology.nework.viewModel.PermissionViewModel
import ru.netology.nework.viewModel.UserViewModel
import java.time.Instant


class NewEventFragment : Fragment() {

    private lateinit var binding: FragmentNewEventBinding
    private lateinit var adapter: SelectedUsersNewEntityAdapter

    private val args: NewEventFragmentArgs by navArgs()
    private val eventViewModel: EventViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val mediaViewModel: MediaViewModel by activityViewModels()
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private val retriever = MediaMetadataRetriever()

    private var editEvent: Event? = null
    private var datetime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestUsers()
        initBinding(inflater, container)
        initAdapter()
        setupListener()
        setupObserver()
        setupData()
        return binding.root
    }

    private fun requestUsers() {
        userViewModel.requestUsers()
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun initAdapter() {
        adapter = SelectedUsersNewEntityAdapter(object : SelectedUsersNewEntityListener {
            override fun deleteSelectedUser(userid: Int) {
                userViewModel.deleteSelectedUser(userid)
            }
        })
        binding.speakersList.adapter = adapter
    }

    private fun setupListener() {
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
            deleteMedia.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                mediaViewModel.deleteMedia()
            }
            addSpeaker.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                AddMentionUser(userViewModel).show(parentFragmentManager, "dialog")
            }
            addEvent.setOnClickListener {
                AndroidUtils.hideKeyboard(it)
                sendEvent()
            }
            dateText.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.selected_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                datePicker.show(parentFragmentManager, "DatePicker")

                datePicker.addOnPositiveButtonClickListener {
                    val date = datePicker.selection
                    date?.let {
                        datetime = AppUtils.setDate(Instant.ofEpochMilli(it).toString(), datetime)
                        dateText.setText(AppUtils.getDate(datetime))
                    }
                }
            }
            timeText.setOnClickListener {
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setTitleText(getString(R.string.selected_time))
                    .build()
                timePicker.show(parentFragmentManager, "TimePicker")

                timePicker.addOnPositiveButtonClickListener {
                    val hour = timePicker.hour
                    val minute = timePicker.minute
                    datetime = AppUtils.setTime(hour.toString(), minute.toString(), datetime)
                    timeText.setText(AppUtils.getTime(datetime))
                }
            }
        }
    }

    private fun sendEvent() {
        binding.apply {
            if (checkRequiredField()) {
                if (editEvent != null) {
                    editEvent?.let { eventViewModel.saveId(it.id) }
                }
                val content = messageText.text.toString()
                eventViewModel.saveContent(content)
                val link = linkText.text.toString().ifEmpty { null }
                val type = when(typeEvent.checkedButtonIds.first()) {
                    R.id.offlineType -> EventType.OFFLINE
                    R.id.onlineType -> EventType.ONLINE
                    else -> return
                }
                eventViewModel.saveType(type)
                eventViewModel.saveDatetime(datetime)
                eventViewModel.saveLink(link)
                eventViewModel.saveSpeakersIds(userViewModel.getSelectedUserIds())
                eventViewModel.saveEvent()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), R.string.content_empty, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkRequiredField(): Boolean {
        binding.apply {
            return messageText.text.toString().isNotEmpty()
                    && dateText.text.toString().isNotEmpty() && timeText.text.toString().isNotEmpty()
        }
    }

    private fun setupObserver() {
        userViewModel.selectedUsers.observe(viewLifecycleOwner) { userList ->
            when (userList) {
                emptyList<User>(), null -> {
                    binding.speakersGroup.visibility = View.GONE
                }
                else -> {
                    binding.speakersGroup.visibility = View.VISIBLE
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

    private fun setupData() {
        editEvent = args.editEvent
        binding.apply {
            if (editEvent != null) {
                editEvent?.let { event ->
                    messageText.setText(event.content)
                    when (event.type) {
                        EventType.OFFLINE -> typeEvent.check(R.id.offlineType)
                        EventType.ONLINE -> typeEvent.check(R.id.onlineType)
                    }
                    datetime = event.datetime
                    dateText.setText(AppUtils.getDate(datetime))
                    timeText.setText(AppUtils.getTime(datetime))
                    if (event.link != null) {
                        linkGroup.visibility = View.VISIBLE
                        addLink.visibility = View.GONE
                        linkText.setText(event.link)
                    } else {
                        linkGroup.visibility = View.GONE
                        addLink.visibility = View.VISIBLE
                    }
                    mediaViewModel.setAttachment(event.attachment)
                    userViewModel.setupUsersFromSelectedIds(event.speakerIds)
                }
            } else {
                linkGroup.visibility = View.GONE
                deleteMedia.visibility = View.GONE
                photoContent.visibility = View.GONE
                videoGroup.visibility = View.GONE
                audioGroup.visibility = View.GONE
                speakersGroup.visibility = View.GONE
            }
        }
    }

}