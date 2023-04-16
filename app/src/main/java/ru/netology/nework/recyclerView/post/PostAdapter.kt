package ru.netology.nework.recyclerView.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.Post

class PostAdapter(
    private val postListener: PostListener
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        payloads.forEach {
            (it as? PostPayload)?.let { payload ->
                val post = getItem(position) ?: return
                holder.bind(payload, post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, postListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }

}