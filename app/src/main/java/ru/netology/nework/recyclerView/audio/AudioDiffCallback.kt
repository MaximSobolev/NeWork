package ru.netology.nework.recyclerView.audio

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nework.dto.AudioFile

class AudioDiffCallback : DiffUtil.ItemCallback<AudioFile>() {
    override fun areItemsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: AudioFile, newItem: AudioFile): Boolean {
        return oldItem.uri == newItem.uri
    }
}