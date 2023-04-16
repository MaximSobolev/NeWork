package ru.netology.nework.recyclerView.users

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nework.dto.User

class UsersDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

}