package ru.netology.nework.dto

data class EventCreate (
    val id : Int = 0,
    val content : String = "",
    val datetime : String = "",
    val coords : Coordinates? = null,
    val type : EventType? = null,
    val attachment: Media? = null,
    val link : String? = null,
    val speakerIds : List<Int> = emptyList()
        )