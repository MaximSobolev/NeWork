package ru.netology.nework.recyclerView.users

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nework.R
import ru.netology.nework.databinding.CardUserBinding
import ru.netology.nework.dto.User

class UsersViewHolder(
    private val binding : CardUserBinding,
    private val usersListener: UsersListener
) : ViewHolder(binding.root) {
    private var itemUser : User? = null

    private val userOnClickListener : View.OnClickListener =
        View.OnClickListener { itemUser?.let { usersListener.goToUserProfile(it.id) } }

    init {
        binding.userContainer.setOnClickListener(userOnClickListener)
    }

    fun bind (user: User) {
        binding.apply {
            if (user.avatar != null) {
                Glide.with(mentionedUserAvatar)
                    .load(user.avatar)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_error_outline_24)
                    .timeout(10_000)
                    .apply(RequestOptions().circleCrop())
                    .into(mentionedUserAvatar)
            } else {
                mentionedUserAvatar.setImageResource(R.drawable.outline_account_circle_24)
            }
            mentionedUserName.text = user.name
        }
        itemUser = user
    }
}