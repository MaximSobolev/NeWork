package ru.netology.nework.recyclerView.audio

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.netology.nework.databinding.CardAudioFileBinding
import ru.netology.nework.dto.AudioFile

class AudioViewHolder(
    private val binding : CardAudioFileBinding,
    private val audioListener: AudioListener
) : ViewHolder(binding.root) {
    private var itemAudio : AudioFile? = null
    private val audioOnClickListener : View.OnClickListener =
        View.OnClickListener {
            itemAudio?.let {audioFile -> audioListener.setAudioFile(audioFile) }
        }

    init {
        binding.audioContainer.setOnClickListener(audioOnClickListener)
    }

    fun bind (audio : AudioFile) {
        binding.apply {
            nameAudio.text = audio.name
            durationAudio.text = getDuration(audio.duration)
        }
        itemAudio = audio
    }

    private fun getDuration(millis : Int) : String {
        val minute = (millis / 1000) / 60
        val second = (millis / 1000) % 60
        return "$minute:$second"
    }
}