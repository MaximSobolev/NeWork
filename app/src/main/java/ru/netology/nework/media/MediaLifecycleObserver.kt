package ru.netology.nework.media

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer? = MediaPlayer()
    val playButton : LiveData<Boolean>
        get() = _playButton
    private val _playButton = MutableLiveData(false)

    fun prepare() {
        player?.prepareAsync()
    }

    fun start() {
        player?.start()
        _playButton.postValue(true)
        player?.setOnCompletionListener {
            _playButton.postValue(false)
        }
    }

    fun pause() {
        player?.pause()
        _playButton.postValue(false)
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE,
            Lifecycle.Event.ON_STOP -> {
                player?.release()
                player = null
            }
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            else -> Unit
        }
    }


}