package ru.netology.nework.recyclerView.event

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nework.dto.Event

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return (oldItem.authorId == newItem.authorId
                && oldItem.author == newItem.author
                && oldItem.authorAvatar == newItem.authorAvatar
                && oldItem.authorJob == newItem.authorJob
                && oldItem.content == newItem.content
                && oldItem.datetime == newItem.datetime
                && oldItem.published == newItem.published
                && oldItem.coords == newItem.coords
                && oldItem.type == newItem.type
                && oldItem.likeOwnerIds.size == newItem.likeOwnerIds.size
                && oldItem.likedByMe == newItem.likedByMe
                && oldItem.participantsIds.size == newItem.participantsIds.size
                && oldItem.participatedByMe == newItem.participatedByMe
                && oldItem.attachment?.url == newItem.attachment?.url
                && oldItem.link == newItem.link
                && oldItem.ownedByMe == newItem.ownedByMe
                && oldItem.users.size == newItem.users.size)
    }

    override fun getChangePayload(oldItem: Event, newItem: Event): Any =
        EventPayload(
            authorJob = newItem.authorJob.takeIf { it != oldItem.authorJob },
            content = newItem.content.takeIf { it != oldItem.content },
            datetime = newItem.datetime.takeIf { it != oldItem.datetime },
            published = newItem.published.takeIf { it != oldItem.published },
            type = newItem.type.takeIf { it != oldItem.type },
            likeOwnerIds = newItem.likeOwnerIds.takeIf { it.size != oldItem.likeOwnerIds.size },
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            speakerIds = newItem.speakerIds.takeIf { it.size != oldItem.speakerIds.size },
            participantsIds = newItem.participantsIds.takeIf { it.size != oldItem.participantsIds.size },
            participatedByMe = newItem.participatedByMe.takeIf { it != oldItem.participatedByMe },
            attachment = newItem.attachment.takeIf { it?.url != oldItem.attachment?.url },
            link = newItem.link.takeIf { it != oldItem.link },
            users = newItem.users.takeIf { it.size != oldItem.users.size },
        )
}