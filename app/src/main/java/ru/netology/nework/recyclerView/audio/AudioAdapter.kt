package ru.netology.nework.recyclerView.audio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.CardAudioFileBinding
import ru.netology.nework.dto.AudioFile

class AudioAdapter(
    private val audioListener: AudioListener
) : ListAdapter<AudioFile, AudioViewHolder>(AudioDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
       val binding = CardAudioFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioViewHolder(binding, audioListener)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}