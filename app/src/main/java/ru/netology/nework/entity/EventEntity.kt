package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.nework.dataBase.converter.EventTypeConverter
import ru.netology.nework.dataBase.converter.ListIntConverter
import ru.netology.nework.dataBase.converter.MapIntUserPreviewConverter
import ru.netology.nework.dto.*

@Entity
@TypeConverters(value = [ListIntConverter::class, MapIntUserPreviewConverter::class, EventTypeConverter::class])
class EventEntity(
    @PrimaryKey
    val id: Int = 0,
    val authorId: Int = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String = "",
    val datetime: String = "",
    val published: String = "",
    @Embedded
    val coords: Coordinates? = null,
    val eventType: EventType = EventType.OFFLINE,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean = false,
    val speakerIds : List<Int> = emptyList(),
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val users: Map<Int, UserPreview> = emptyMap()
) {

    fun toDto(): Event = Event(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        datetime,
        published,
        coords,
        eventType,
        likeOwnerIds,
        likedByMe,
        speakerIds,
        participantsIds,
        participatedByMe,
        attachment,
        link,
        ownedByMe,
        users
    )
    companion object {
        fun fromDto(event: Event) : EventEntity = EventEntity(
            event.id,
            event.authorId,
            event.author,
            event.authorAvatar,
            event.authorJob,
            event.content,
            event.datetime,
            event.published,
            event.coords,
            event.type,
            event.likeOwnerIds,
            event.likedByMe,
            event.speakerIds,
            event.participantsIds,
            event.participatedByMe,
            event.attachment,
            event.link,
            event.ownedByMe,
            event.users
        )
    }
}