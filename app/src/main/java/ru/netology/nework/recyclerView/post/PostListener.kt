package ru.netology.nework.recyclerView.post

import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post
import ru.netology.nework.media.MediaLifecycleObserver

interface PostListener {
    fun addObserver(mediaObserver : MediaLifecycleObserver)
    fun removeObserver(mediaObserver: MediaLifecycleObserver)
    fun likeById(post : Post)
    fun goToUserProfile(id : Int)
    fun onRemove(id : Int)
    fun onEdit(post : Post)
    fun openMedia(attachment: Attachment)
}