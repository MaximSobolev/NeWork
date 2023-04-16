package ru.netology.nework.recyclerView.event

import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Event
import ru.netology.nework.media.MediaLifecycleObserver

interface EventListener {
    fun addObserver(mediaObserver : MediaLifecycleObserver)
    fun removeObserver(mediaObserver: MediaLifecycleObserver)
    fun goToUserProfile(id : Int)
    fun likeEvent(event : Event)
    fun participateEvent(event : Event)
    fun onRemove(id : Int)
    fun onEdit(event : Event)
    fun openMedia(attachment: Attachment)
}