package ru.netology.nework.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.*
import ru.netology.nework.model.MediaModel
import ru.netology.nework.repository.MediaRepository
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    val photo: LiveData<MediaModel?> = repository.photo
    val video: LiveData<MediaModel?> = repository.video
    val audioFiles: LiveData<List<AudioFile>?> = repository.audioFiles
    val audioContent: LiveData<AudioFile?> = repository.audio
    val attach: LiveData<Attachment?> = repository.attach

    fun onPhotoPickerSelect(uri: Uri?) {
        uri?.let {
            viewModelScope.launch {
                repository.setPhoto(uri)
            }
        }
    }

    fun onVideoPickerSelect(uri: Uri?) {
        uri?.let {
            viewModelScope.launch {
                repository.setVideo(uri)
            }
        }
    }

    fun setAudioList(audioList: List<AudioFile>) {
        repository.setAudioList(audioList)
    }

    fun setAudioFile(audio: AudioFile) {
        viewModelScope.launch {
            repository.setAudio(audio)
        }
    }

    fun deleteMedia() {
        viewModelScope.launch {
            repository.deleteMedia()
        }
    }

    fun setAttachment(attach: Attachment?) {
        repository.setAttach(attach)
    }

}