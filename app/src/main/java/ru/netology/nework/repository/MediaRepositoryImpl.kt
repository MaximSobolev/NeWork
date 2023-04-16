package ru.netology.nework.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nework.dto.*
import ru.netology.nework.model.MediaModel
import java.io.File
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    @ApplicationContext
    private val context: Context
) : MediaRepository {

    private val cachePhotoFolder = File(context.cacheDir, "photos").also { it.mkdir() }
    private val cacheVideoFolder = File(context.cacheDir, "video").also { it.mkdir() }
    private val cacheAudioFolder = File(context.cacheDir, "audio").also { it.mkdir() }

    override val photo: LiveData<MediaModel?>
        get() = _photo
    private val _photo = MutableLiveData<MediaModel?>(null)

    override val video: LiveData<MediaModel?>
        get() = _video

    override val audioFiles: LiveData<List<AudioFile>?>
        get() = _audioFiles
    private val _audioFiles = MutableLiveData<List<AudioFile>?>(null)


    private val _video = MutableLiveData<MediaModel?>(null)

    override val audio: LiveData<AudioFile?>
        get() = _audio
    private val _audio = MutableLiveData<AudioFile?>(null)

    override val attach: LiveData<Attachment?>
        get() = _attach
    private val _attach = MutableLiveData<Attachment?>(null)

    override suspend fun onPhotoPickerSelect(uri: Uri): File? {
        var resultPhoto: File? = null
        withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { input ->
                val cachedPhoto = generatePhotoCacheFile()

                cachedPhoto.outputStream().use { output ->
                    input.copyTo(output)
                    resultPhoto = cachedPhoto
                }
            }
        }
        return resultPhoto
    }

    override suspend fun onVideoPickerSelect(uri: Uri): File? {
        var resultVideo: File? = null
        withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { input ->
                val cachedVideo = generateVideoCacheFile()

                cachedVideo.outputStream().use { output ->
                    input.copyTo(output)
                    resultVideo = cachedVideo
                }
            }
        }
        return resultVideo
    }

    override suspend fun onAudioSelect(uri: Uri): File? {
        var resultAudio: File? = null
        withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { input ->
                val cachedAudio = generateAudioCacheFile()

                cachedAudio.outputStream().use { output ->
                    input.copyTo(output)
                    resultAudio = cachedAudio
                }
            }
        }
        return resultAudio
    }

    override suspend fun deletePhotoFile(file: File): Boolean {
        var result: Boolean
        withContext(Dispatchers.IO) {
            result = file.delete()
        }
        return result
    }

    override suspend fun deleteVideoFile(file: File): Boolean {
        var result: Boolean
        withContext(Dispatchers.IO) {
            result = file.delete()
        }
        return result
    }

    override suspend fun deleteAudioFile(file: File): Boolean {
        var result: Boolean
        withContext(Dispatchers.IO) {
            result = file.delete()
        }
        return result
    }

    override fun generatePhotoCacheFile() = File(cachePhotoFolder, generatePhotoFileName())
    override fun generatePhotoFileName() = "${System.currentTimeMillis()}.jpg"
    override fun generateVideoCacheFile(): File = File(cacheVideoFolder, generateVideoFileName())
    override fun generateVideoFileName(): String = "${System.currentTimeMillis()}.mp4"
    override fun generateAudioCacheFile(): File = File(cacheAudioFolder, generateAudioFileName())
    override fun generateAudioFileName(): String = "${System.currentTimeMillis()}.mp3"

    override suspend fun setPhoto(uri: Uri) {
        val file = onPhotoPickerSelect(uri)
        file?.let {
            _photo.postValue(MediaModel(uri, file))
        }
    }

    override suspend fun setVideo(uri: Uri) {
        val file = onVideoPickerSelect(uri)
        file?.let {
            _video.postValue(MediaModel(uri, file))
        }
    }

    override suspend fun setAudio(audio: AudioFile) {
        val file = onAudioSelect(audio.uri)
        file?.let {
            _audio.postValue(audio.copy(file = it))
        }
    }

    override fun setAttach(attach: Attachment?) {
        _attach.postValue(attach)
    }

    override fun getAttach(): Media? {
        var attachment: Media? = null
        photo.value?.let { photoContent ->
            attachment = CreateAttachment(photoContent.file, AttachType.IMAGE)
        }
        video.value?.let { videoContent ->
            attachment = CreateAttachment(videoContent.file, AttachType.VIDEO)
        }
        audio.value?.let { audioContent ->
            audioContent.file?.let { audioFile ->
                attachment = CreateAttachment(audioFile, AttachType.AUDIO)
            }
        }
        attach.value?.let {
            attachment = it
        }

        return attachment
    }

    override fun setAudioList(audioList: List<AudioFile>) {
        _audioFiles.postValue(audioList)
    }

    override fun deleteAudioList() {
        _audioFiles.postValue(null)
    }

    override suspend fun deleteMedia() {
        _photo.value?.let {
            if (deletePhotoFile(it.file)) {
                _photo.postValue(null)
            }
        }
        _video.value?.let {
            if (deleteVideoFile(it.file)) {
                _video.postValue(null)
            }
        }
        _audio.value?.let { audioFile ->
            audioFile.file?.let { file ->
                deleteAudioFile(file)
            }
            _audio.postValue(null)
        }
        _attach.postValue(null)
    }
}