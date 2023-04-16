package ru.netology.nework.entity

import androidx.room.*
import ru.netology.nework.dataBase.converter.ListIntConverter
import ru.netology.nework.dataBase.converter.MapIntUserPreviewConverter
import ru.netology.nework.dto.*

@Entity
@TypeConverters(value = [ListIntConverter::class, MapIntUserPreviewConverter::class])
data class PostEntity(
    @PrimaryKey
    val id: Int = 0,
    val authorId: Int = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String = "",
    val published: String = "",
    @Embedded
    val coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Map<Int, UserPreview> = emptyMap()
) {
    fun toDto(): Post = Post(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        published,
        coords,
        link,
        likeOwnerIds,
        mentionIds,
        mentionedMe,
        likedByMe,
        attachment,
        ownedByMe,
        users
    )
    companion object {
        fun fromDto(post : Post) = PostEntity(
            post.id,
            post.authorId,
            post.author,
            post.authorAvatar,
            post.authorJob,
            post.content,
            post.published,
            post.coords,
            post.link,
            post.likeOwnerIds,
            post.mentionIds,
            post.mentionedMe,
            post.likedByMe,
            post.attachment,
            post.ownedByMe,
            post.users
        )
    }
}