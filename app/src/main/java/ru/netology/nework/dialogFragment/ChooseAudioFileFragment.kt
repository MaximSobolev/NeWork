package ru.netology.nework.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.DialogChooseAudioFileBinding
import ru.netology.nework.dto.AudioFile
import ru.netology.nework.recyclerView.audio.AudioAdapter
import ru.netology.nework.recyclerView.audio.AudioListener
import ru.netology.nework.viewModel.MediaViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ChooseAudioFileFragment @Inject constructor(
    private val mediaViewModel: MediaViewModel
) : DialogFragment() {

    private val audioObserver: Observer<List<AudioFile>?> = Observer<List<AudioFile>?> {
        it?.let {list ->
            adapter.submitList(list)
        }
    }

    private lateinit var binding: DialogChooseAudioFileBinding
    private lateinit var adapter: AudioAdapter
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initBinding()
        setupAdapter()
        setupObserver()
        buildDialog()
        dismissListener()
        return dialog
    }

    private fun initBinding() {
        binding = DialogChooseAudioFileBinding.inflate(layoutInflater)
    }

    private fun setupAdapter() {
        adapter = AudioAdapter(object : AudioListener {
                override fun setAudioFile(audio: AudioFile) {
                    mediaViewModel.setAudioFile(audio)
                    dialog.dismiss()
                }
            }
        )
        binding.userList.adapter = adapter
    }

    private fun setupObserver() {
        mediaViewModel.audioFiles.observeForever(audioObserver)
    }

    private fun buildDialog() {
        builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
        dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun dismissListener() {
        dialog.setOnDismissListener {
            mediaViewModel.audioFiles.removeObserver(audioObserver)
        }
    }
}