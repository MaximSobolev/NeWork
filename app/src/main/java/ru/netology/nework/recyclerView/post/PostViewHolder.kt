package ru.netology.nework.recyclerView.post

import android.media.MediaPlayer
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nework.R
import ru.netology.nework.databinding.CardPostBinding
import ru.netology.nework.dto.AttachType
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.media.MediaLifecycleObserver
import ru.netology.nework.recyclerView.users.UsersAdapter
import ru.netology.nework.recyclerView.users.UsersListener
import ru.netology.nework.utils.AppUtils

class PostViewHolder(
    private val binding: CardPostBinding,
    private val postListener: PostListener
) : ViewHolder(binding.root) {
    private var itemPost: Post? = null
    private lateinit var adapter: UsersAdapter
    private val mediaObserver = MediaLifecycleObserver()
    private val playButtonObserver : Observer<Boolean> = Observer<Boolean> {
        binding.playAudio.isChecked = it
    }


    private val userProfileOnClickListener: View.OnClickListener =
        View.OnClickListener { itemPost?.let { postListener.goToUserProfile(it.authorId) } }
    private val openMediaOnCLickListener : View.OnClickListener =
        View.OnClickListener { itemPost?.attachment?.let { postListener.openMedia(it) } }
    private val likeButtonOnClickListener: View.OnClickListener =
        View.OnClickListener { itemPost?.let { postListener.likeById(it) } }
    private val menuButtonOnClickListener: View.OnClickListener =
        View.OnClickListener { view ->
            itemPost?.let { post ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owner, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                postListener.onEdit(post)
                                true
                            }
                            R.id.remove -> {
                                postListener.onRemove(post.id)
                                true
                            }
                            else -> {
                                false
                            }
                        }

                    }
                }.show()
            }

        }

    private val playAudioOnCLickListener : View.OnClickListener =
        View.OnClickListener {
            if (mediaObserver.playButton.value!!) {
                mediaObserver.pause()
            } else {
                mediaObserver.start()
            }
        }


    init {
        binding.apply {
            avatar.setOnClickListener(userProfileOnClickListener)
            name.setOnClickListener(userProfileOnClickListener)
            likeButton.setOnClickListener(likeButtonOnClickListener)
            menuButton.setOnClickListener(menuButtonOnClickListener)
            playAudio.setOnClickListener(playAudioOnCLickListener)
            photoContent.setOnClickListener(openMediaOnCLickListener)
            playVideo.setOnClickListener(openMediaOnCLickListener)
        }
    }


    fun bind(post: Post) {
        binding.apply {
            if (post.authorAvatar != null) {
                Glide.with(avatar)
                    .load(post.authorAvatar)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_error_outline_24)
                    .timeout(10_000)
                    .apply(RequestOptions().circleCrop())
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.outline_account_circle_24)
            }

            if (post.attachment != null) {
                when (post.attachment.type) {
                    AttachType.IMAGE -> {
                        photoContent.visibility = View.VISIBLE
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        Glide.with(photoContent)
                            .load(post.attachment.url)
                            .placeholder(R.drawable.baseline_downloading_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .into(photoContent)

                        mediaObserver.player?.release()
                        mediaObserver.playButton.removeObserver(playButtonObserver)
                    }
                    AttachType.VIDEO -> {
                        Glide.with(videoPreview)
                            .load(post.attachment.url)
                            .placeholder(R.drawable.baseline_downloading_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .into(videoPreview)
                        videoGroup.visibility = View.VISIBLE
                        photoContent.visibility = View.GONE
                        audioGroup.visibility = View.GONE

                        mediaObserver.player?.release()
                        mediaObserver.playButton.removeObserver(playButtonObserver)
                    }
                    AttachType.AUDIO -> {
                        initMediaPlayer(post.attachment.url)
                        mediaObserver.playButton.observeForever(playButtonObserver)
                        videoGroup.visibility = View.GONE
                        photoContent.visibility = View.GONE
                        audioGroup.visibility = View.VISIBLE
                    }
                }
            } else {
                videoGroup.visibility = View.GONE
                photoContent.visibility = View.GONE
                audioGroup.visibility = View.GONE

                mediaObserver.player?.release()
                mediaObserver.playButton.removeObserver(playButtonObserver)
                postListener.removeObserver(mediaObserver)
            }

            name.text = post.author
            if (post.authorJob != null) {
                job.visibility = View.VISIBLE
                job.text = post.authorJob
            } else {
                job.visibility = View.GONE
            }
            published.text = AppUtils.parseDateTime(post.published)
            content.text = post.content
            likeButton.text = AppUtils.largeNumberDisplay(post.likeOwnerIds.size)
            likeButton.isChecked = post.likedByMe
            menuButton.isVisible = post.ownedByMe
            if (post.link != null) {
                linkGroup.visibility = View.VISIBLE
                link.text = post.link
            } else {
                linkGroup.visibility = View.GONE
            }
            if (post.mentionIds.isNotEmpty()) {
                mentionedUserGroup.visibility = View.VISIBLE
                initAdapter()
                mentionedUserList.adapter = adapter
                adapter.submitList(getMentionUsers(post.users, post.mentionIds))
            } else {
                mentionedUserGroup.visibility = View.GONE
            }
            itemPost = post
        }
    }

    fun bind(payload: PostPayload , post : Post) {
        binding.apply {
            payload.authorJob?.let {
                job.visibility = View.VISIBLE
                job.text = it
            }
            payload.content?.let {
                content.text = it
            }
            payload.published?.let {
                published.text = AppUtils.parseDateTime(it)
            }
            payload.link?.let {
                linkGroup.visibility = View.VISIBLE
                link.text = it
            }
            payload.likeOwnerIds?.let {
                likeButton.text = AppUtils.largeNumberDisplay(it.size)
            }
            payload.mentionIds?.let {mentionIds ->
                payload.users?.let {users ->
                    if (mentionIds.isNotEmpty()) {
                        mentionedUserGroup.visibility = View.VISIBLE
                        initAdapter()
                        mentionedUserList.adapter = adapter
                        adapter.submitList(getMentionUsers(users, mentionIds))
                    } else {
                        mentionedUserGroup.visibility = View.GONE
                    }
                }
            }
            payload.likedByMe?.let {
                likeButton.isChecked = it
            }
            payload.attachment?.let {
                when (it.type) {
                    AttachType.IMAGE -> {
                        photoContent.visibility = View.VISIBLE
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        Glide.with(photoContent)
                            .load(it.url)
                            .placeholder(R.drawable.baseline_downloading_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .into(photoContent)

                        mediaObserver.player?.release()
                        mediaObserver.playButton.removeObserver(playButtonObserver)
                    }
                    AttachType.VIDEO -> {
                        Glide.with(videoPreview)
                            .load(it.url)
                            .placeholder(R.drawable.baseline_downloading_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .into(videoPreview)
                        videoGroup.visibility = View.VISIBLE
                        photoContent.visibility = View.GONE
                        audioGroup.visibility = View.GONE

                        mediaObserver.player?.release()
                        mediaObserver.playButton.removeObserver(playButtonObserver)
                    }
                    AttachType.AUDIO -> {
                        initMediaPlayer(it.url)
                        mediaObserver.playButton.observeForever(playButtonObserver)
                        videoGroup.visibility = View.GONE
                        photoContent.visibility = View.GONE
                        audioGroup.visibility = View.VISIBLE
                    }
                }
            }
        }
        itemPost = post
    }

    private fun initAdapter() {
        adapter = UsersAdapter(object : UsersListener {
            override fun goToUserProfile(id: Int) {
                postListener.goToUserProfile(id)
            }

        })

    }

    private fun getMentionUsers(map: Map<Int, UserPreview>, mentionIds: List<Int>): List<User> {
        var users: List<User> = emptyList()
        mentionIds.map { mentionId ->
            if (map.containsKey(mentionId)) {
                val userPreview = map.getValue(mentionId)
                users = users + listOf(User(mentionId, "", userPreview.name, userPreview.avatar))
            }
        }
        return users
    }

    private fun initMediaPlayer(url : String) {
        mediaObserver.player = MediaPlayer()
        postListener.addObserver(mediaObserver)
        mediaObserver.player?.setDataSource(url)
        mediaObserver.prepare()
        mediaObserver.player?.let {
            it.setOnCompletionListener { binding.playAudio.isChecked = false }
        }
    }
}