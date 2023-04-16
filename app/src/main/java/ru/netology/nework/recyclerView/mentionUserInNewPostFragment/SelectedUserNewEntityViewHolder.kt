package ru.netology.nework.recyclerView.mentionUserInNewPostFragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nework.R
import ru.netology.nework.databinding.CardMentionedUserAddPostBinding
import ru.netology.nework.dto.User

class SelectedUserNewEntityViewHolder (
    private val binding: CardMentionedUserAddPostBinding,
    private val mentionUserNewPostListener: SelectedUsersNewEntityListener
) : RecyclerView.ViewHolder(binding.root) {
    private var item: User? = null
    private val deleteOnClickListener: View.OnClickListener =
        View.OnClickListener { item?.let { mentionUserNewPostListener.deleteSelectedUser(it.id) } }

    init {
        binding.deleteUser.setOnClickListener(deleteOnClickListener)
    }

    fun bind(user: User) {
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

        item = user
    }
}