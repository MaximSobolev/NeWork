package ru.netology.nework.recyclerView.mentionUserInNewPostFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.CardMentionedUserAddPostBinding
import ru.netology.nework.dto.User
import ru.netology.nework.recyclerView.users.UsersDiffCallback

class SelectedUsersNewEntityAdapter(
    private val selectedUsersNewEntityListener: SelectedUsersNewEntityListener
) : ListAdapter<User, SelectedUserNewEntityViewHolder>(UsersDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedUserNewEntityViewHolder {
        val binding = CardMentionedUserAddPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectedUserNewEntityViewHolder(binding, selectedUsersNewEntityListener)
    }

    override fun onBindViewHolder(holder: SelectedUserNewEntityViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}