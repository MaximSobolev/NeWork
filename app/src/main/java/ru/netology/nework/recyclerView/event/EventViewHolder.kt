package ru.netology.nework.recyclerView.event

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nework.R
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.*
import ru.netology.nework.media.MediaLifecycleObserver
import ru.netology.nework.recyclerView.users.UsersAdapter
import ru.netology.nework.recyclerView.users.UsersListener
import ru.netology.nework.utils.AppUtils

class EventViewHolder(
    private val binding: CardEventBinding,
    private val eventListener: EventListener,
    private val context: Context
) : ViewHolder(binding.root) {

    private lateinit var adapter: UsersAdapter

    private var itemEvent: Event? = null

    private val mediaObserver = MediaLifecycleObserver()
    private val playButtonObserver: Observer<Boolean> = Observer<Boolean> {
        binding.playAudio.isChecked = it
    }

    private val userProfileOnClickListener: View.OnClickListener =
        View.OnClickListener { itemEvent?.let { eventListener.goToUserProfile(it.authorId) } }
    private val openMediaOnCLickListener: View.OnClickListener =
        View.OnClickListener { itemEvent?.attachment?.let { eventListener.openMedia(it) } }
    private val likeButtonOnClickListener: View.OnClickListener =
        View.OnClickListener { itemEvent?.let { eventListener.likeEvent(it) } }
    private val participateOnClickListener: View.OnClickListener =
        View.OnClickListener { itemEvent?.let { eventListener.participateEvent(it) } }
    private val menuButtonOnClickListener: View.OnClickListener =
        View.OnClickListener { view ->
            itemEvent?.let { event ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owner, event.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                eventListener.onEdit(event)
                                true
                            }
                            R.id.remove -> {
                                eventListener.onRemove(event.id)
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
    private val playAudioOnCLickListener: View.OnClickListener =
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
            participate.setOnClickListener(participateOnClickListener)
            photoContent.setOnClickListener(openMediaOnCLickListener)
            playVideo.setOnClickListener(openMediaOnCLickListener)
            playAudio.setOnClickListener(playAudioOnCLickListener)
            menuButton.setOnClickListener(menuButtonOnClickListener)
        }
    }

    fun bind(event: Event) {
        binding.apply {
            if (event.authorAvatar != null) {
                Glide.with(avatar)
                    .load(event.authorAvatar)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_error_outline_24)
                    .timeout(10_000)
                    .apply(RequestOptions().circleCrop())
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.outline_account_circle_24)
            }

            if (event.attachment != null) {
                when (event.attachment.type) {
                    AttachType.IMAGE -> {
                        photoContent.visibility = View.VISIBLE
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        Glide.with(photoContent)
                            .load(event.attachment.url)
                            .placeholder(R.drawable.baseline_downloading_24)
                            .error(R.drawable.outline_error_outline_24)
                            .timeout(10_000)
                            .into(photoContent)

                        mediaObserver.player?.release()
                        mediaObserver.playButton.removeObserver(playButtonObserver)
                    }
                    AttachType.VIDEO -> {
                        Glide.with(videoPreview)
                            .load(event.attachment.url)
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
                        initMediaPlayer(event.attachment.url)
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
                eventListener.removeObserver(mediaObserver)
            }

            name.text = event.author
            menuButton.isVisible = event.ownedByMe

            if (event.authorJob != null) {
                job.visibility = View.VISIBLE
                job.text = event.authorJob
            } else {
                job.visibility = View.GONE
            }

            published.text = AppUtils.parseDateTime(event.published)
            content.text = event.content

            if (event.link != null) {
                linkGroup.visibility = View.VISIBLE
                link.text = event.link
            } else {
                linkGroup.visibility = View.GONE
            }

            dateTime.text = AppUtils.parseDateTime(event.datetime)
            eventType.text = event.type.toString()
            numberOfParticipants.text = event.participantsIds.size.toString()

            if (event.speakerIds.isNotEmpty()) {
                speakersUserGroup.visibility = View.VISIBLE
                initAdapter()
                speakersUserList.adapter = adapter
                adapter.submitList(getMentionUsers(event.users, event.speakerIds))
            } else {
                speakersUserGroup.visibility = View.GONE
            }

            likeButton.text = AppUtils.largeNumberDisplay(event.likeOwnerIds.size)
            likeButton.isChecked = event.likedByMe
            participate.isChecked = event.participatedByMe
            participate.text = if (event.participatedByMe) {
                context.getString(R.string.cancel_participation)
            } else {
                context.getString(R.string.participate)
            }
        }
        itemEvent = event
    }

    fun bind(payload: EventPayload, event : Event) {
        binding.apply {
            payload.authorJob?.let {
                job.visibility = View.VISIBLE
                job.text = it
            }
            payload.content?.let {
                content.text = it
            }
            payload.datetime?.let {
                dateTime.text = AppUtils.parseDateTime(it)
            }
            payload.published?.let {
                published.text = AppUtils.parseDateTime(it)
            }
            payload.type?.let {
                eventType.text = it.toString()
            }
            payload.likeOwnerIds?.let {
                likeButton.text = AppUtils.largeNumberDisplay(it.size)
            }
            payload.likedByMe?.let {
                likeButton.isChecked = it
            }
            payload.speakerIds?.let { speakers ->
                payload.users?.let { users->
                    if (speakers.isNotEmpty()) {
                        speakersUserGroup.visibility = View.VISIBLE
                        initAdapter()
                        speakersUserList.adapter = adapter
                        adapter.submitList(getMentionUsers(users, speakers))
                    } else {
                        speakersUserGroup.visibility = View.GONE
                    }
                }
            }
            payload.participantsIds?.let {
                numberOfParticipants.text = it.size.toString()
            }
            payload.participatedByMe?.let {
                participate.isChecked = it
                participate.text = if (it) {
                    context.getString(R.string.cancel_participation)
                } else {
                    context.getString(R.string.participate)
                }
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
            payload.link?.let {
                linkGroup.visibility = View.VISIBLE
                link.text = it
            }
        }
        itemEvent = event
    }

    private fun initMediaPlayer(url: String) {
        mediaObserver.player = MediaPlayer()
        eventListener.addObserver(mediaObserver)
        mediaObserver.player?.setDataSource(url)
        mediaObserver.prepare()
        mediaObserver.player?.let {
            it.setOnCompletionListener { binding.playAudio.isChecked = false }
        }
    }

    private fun initAdapter() {
        adapter = UsersAdapter(object : UsersListener {
            override fun goToUserProfile(id: Int) {
                eventListener.goToUserProfile(id)
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
}