package ru.netology.nework.recyclerView.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.User

class UsersAdapter(
    private val mentionUsersListener: UsersListener
) : ListAdapter<User, UsersViewHolder>(UsersDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(binding, mentionUsersListener)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

}