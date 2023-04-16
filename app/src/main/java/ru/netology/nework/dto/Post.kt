package ru.netology.nework.dto


data class Post(
    val id: Int,
    val authorId : Int,
    val author : String,
    val authorAvatar : String?,
    val authorJob : String?,
    val content : String,
    val published : String,
    val coords : Coordinates?,
    val link : String?,
    val likeOwnerIds : List<Int>,
    val mentionIds : List<Int>,
    val mentionedMe : Boolean,
    val likedByMe : Boolean,
    val attachment : Attachment?,
    val ownedByMe : Boolean,
    val users : Map<Int, UserPreview>
    ) : java.io.Serializable