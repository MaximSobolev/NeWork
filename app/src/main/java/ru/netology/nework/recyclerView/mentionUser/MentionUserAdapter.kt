package ru.netology.nework.recyclerView.mentionUser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.CardMentionedUsersBinding
import ru.netology.nework.dto.User
import ru.netology.nework.recyclerView.users.UsersDiffCallback

class MentionUserAdapter(
    private val mentionUserListener: MentionUserListener
) : ListAdapter<User, MentionUserViewHolder>(UsersDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentionUserViewHolder {
        val binding = CardMentionedUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MentionUserViewHolder(binding, mentionUserListener)
    }

    override fun onBindViewHolder(holder: MentionUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}