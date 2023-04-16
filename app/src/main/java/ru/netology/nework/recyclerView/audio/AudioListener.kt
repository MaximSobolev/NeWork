package ru.netology.nework.recyclerView.audio

import ru.netology.nework.dto.AudioFile

interface AudioListener {
    fun setAudioFile(audio : AudioFile)
}