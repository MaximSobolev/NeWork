package ru.netology.nework.recyclerView.post

import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.UserPreview

data class PostPayload (
    val authorJob : String? = null,
    val content : String? = null,
    val published : String? = null,
    val link : String? = null,
    val likeOwnerIds : List<Int>? = null,
    val mentionIds : List<Int>? = null,
    val likedByMe : Boolean? = null,
    val attachment : Attachment? = null,
    val users : Map<Int, UserPreview>? = null
        )