package ru.netology.nework.recyclerView.event

import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.UserPreview

data class EventPayload (
        val authorJob : String? = null,
        val content : String? = null,
        val datetime : String? = null,
        val published : String? = null,
        val type : EventType? = null,
        val likeOwnerIds : List<Int>? = null,
        val likedByMe : Boolean? = null,
        val speakerIds : List<Int>? = null,
        val participantsIds : List<Int>? = null,
        val participatedByMe : Boolean? = null,
        val attachment: Attachment? = null,
        val link : String? = null,
        val users : Map<Int, UserPreview>? = null
        )