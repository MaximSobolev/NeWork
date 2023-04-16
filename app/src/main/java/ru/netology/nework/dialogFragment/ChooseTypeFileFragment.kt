package ru.netology.nework.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.DialogChooseTypeFileBinding
import ru.netology.nework.dto.AudioFile
import ru.netology.nework.viewModel.MediaViewModel
import ru.netology.nework.viewModel.PermissionViewModel

@AndroidEntryPoint
class ChooseTypeFileFragment(
    private val mediaViewModel: MediaViewModel,
    private val permissionViewModel: PermissionViewModel
) : DialogFragment() {

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        mediaViewModel.onPhotoPickerSelect(it)
        dialog.dismiss()
    }
    private val pickVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        mediaViewModel.onVideoPickerSelect(it)
        dialog.dismiss()
    }

    private val requestReadMediaAudioPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (Build.VERSION.SDK_INT < 33) {
                if (isGranted) {
                    permissionViewModel.onPermissionChange(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        isGranted
                    )
                    takeAudio()
                }
            } else {
                permissionViewModel.onPermissionChange(
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    isGranted
                )
                takeAudio()
            }
        }

    private lateinit var binding: DialogChooseTypeFileBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initBinding()
        setupObserver()
        setupListeners()
        buildDialog()
        dismissListener()
        return dialog
    }

    private fun initBinding() {
        binding = DialogChooseTypeFileBinding.inflate(layoutInflater)
    }

    private fun setupObserver() {

    }

    private fun buildDialog() {
        builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupListeners() {
        binding.apply {
            addPhoto.setOnClickListener {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

            }
            addVideo.setOnClickListener {
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
            addAudio.setOnClickListener {
                when {
                    permissionViewModel.canAddAudio() -> takeAudio()
                    else -> {
                        requestReadMediaAudioPermissions.launch(permissionViewModel.getPermission())
                    }
                }
            }
        }
    }

    private fun dismissListener() {
        dialog.setOnDismissListener {

        }
    }

    private fun takeAudio() {
        val cacheList = mediaViewModel.audioFiles.value
        if (cacheList == null || cacheList.isEmpty()) {
            val audioList = mutableListOf<AudioFile>()
            val collection = if (Build.VERSION.SDK_INT < 33) {
                Media.EXTERNAL_CONTENT_URI
            } else {
                Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            }
            val proj = arrayOf(
                Media._ID,
                Media.DISPLAY_NAME,
                Media.DURATION
            )

            activity?.application?.contentResolver?.query(
                collection,
                proj,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val contentUri = ContentUris.withAppendedId(
                        Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val audioFile = AudioFile(contentUri, displayName, duration, null)
                    audioList += audioFile
                }
            }
            mediaViewModel.setAudioList(audioList)
        } else {
            ChooseAudioFileFragment(mediaViewModel).show(parentFragmentManager, "dialog")
        }
        dialog.dismiss()
    }

}