package ru.netology.nework.dto

data class PostCreate (
    val id : Int = 0,
    val content : String = "",
    val coords : Coordinates? = null,
    val link : String? = null,
    val attachment: Media? = null,
    val mentionIds : List<Int> = emptyList()
        )