package ru.netology.nework.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.AudioFile
import ru.netology.nework.dto.Media
import ru.netology.nework.model.MediaModel
import java.io.File

interface MediaRepository {
    val photo : LiveData<MediaModel?>
    val video : LiveData<MediaModel?>
    val audioFiles: LiveData<List<AudioFile>?>
    val audio : LiveData<AudioFile?>
    val attach : LiveData<Attachment?>
    suspend fun onPhotoPickerSelect(uri: Uri): File?
    suspend fun onVideoPickerSelect(uri: Uri): File?
    suspend fun onAudioSelect(uri: Uri): File?
    suspend fun deletePhotoFile(file: File): Boolean
    suspend fun deleteVideoFile(file: File): Boolean
    suspend fun deleteAudioFile(file: File): Boolean
    fun generatePhotoCacheFile(): File
    fun generatePhotoFileName(): String
    fun generateVideoCacheFile(): File
    fun generateVideoFileName(): String
    fun generateAudioCacheFile(): File
    fun generateAudioFileName(): String
    suspend fun setPhoto(uri: Uri)
    suspend fun setVideo(uri: Uri)
    suspend fun setAudio(audio: AudioFile)
    fun setAttach(attach : Attachment?)
    fun getAttach() : Media?
    fun setAudioList(audioList: List<AudioFile>)
    fun deleteAudioList()
    suspend fun deleteMedia()
}