package ru.netology.nework.recyclerView.mentionUser

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nework.R
import ru.netology.nework.databinding.CardMentionedUsersBinding
import ru.netology.nework.dto.User

class MentionUserViewHolder(
    private val binding: CardMentionedUsersBinding,
    private val mentionUserListener: MentionUserListener
) : ViewHolder(binding.root) {
    private var item : User? = null
    private val userOnClickListener : View.OnClickListener =
        View.OnClickListener { item?.let { mentionUserListener.chooseUser(it) } }

    init {
        binding.userContainer.setOnClickListener(userOnClickListener)
    }

    fun bind(user : User) {
        binding.apply {
            if (user.avatar != null) {
                Glide.with(userAvatar)
                    .load(user.avatar)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_error_outline_24)
                    .timeout(10_000)
                    .apply(RequestOptions().circleCrop())
                    .into(userAvatar)
            } else {
                userAvatar.setImageResource(R.drawable.outline_account_circle_24)
            }
            userName.text = user.name
        }

          item = user
    }
}