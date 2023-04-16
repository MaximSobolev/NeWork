package ru.netology.nework.recyclerView.post

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nework.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return (oldItem.authorJob == newItem.authorJob
                && oldItem.content == newItem.content
                && oldItem.published == newItem.published
                && oldItem.link == newItem.link
                && oldItem.likeOwnerIds.size == newItem.likeOwnerIds.size
                && oldItem.mentionIds == newItem.mentionIds
                && oldItem.likedByMe == newItem.likedByMe
                && oldItem.attachment?.url == newItem.attachment?.url
                && oldItem.ownedByMe == newItem.ownedByMe
                && oldItem.users.size == newItem.users.size)
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any =
        PostPayload(
            authorJob = newItem.authorJob.takeIf { it != oldItem.authorJob },
            content = newItem.content.takeIf { it != oldItem.content },
            published = newItem.published.takeIf { it != oldItem.published },
            link = newItem.link.takeIf { it != oldItem.link },
            likeOwnerIds = newItem.likeOwnerIds.takeIf { it.size != oldItem.likeOwnerIds.size },
            mentionIds = newItem.mentionIds.takeIf { it != oldItem.mentionIds },
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            attachment = newItem.attachment.takeIf { it?.url != oldItem.attachment?.url },
            users = newItem.users.takeIf { it.size != oldItem.users.size },
        )
}