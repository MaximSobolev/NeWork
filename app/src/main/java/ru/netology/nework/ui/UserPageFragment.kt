package ru.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentUserPageBinding
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import ru.netology.nework.media.MediaLifecycleObserver
import ru.netology.nework.recyclerView.post.PostAdapter
import ru.netology.nework.recyclerView.post.PostListener
import ru.netology.nework.viewModel.JobsViewModel
import ru.netology.nework.viewModel.PostViewModel
import ru.netology.nework.viewModel.UserViewModel

class UserPageFragment : Fragment() {

    private lateinit var binding: FragmentUserPageBinding
    private lateinit var adapter: PostAdapter
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val jobsViewModel: JobsViewModel by activityViewModels()
    private val argument: UserPageFragmentArgs by navArgs()
    private var userId: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater, container)
        setupData()
        setupVisibility()
        initAdapter()
        setupObserver()
        setupListeners()
        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentUserPageBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupData() {
        userId = argument.userId

        if (userId == 0) {
            findNavController().navigateUp()
            return
        }

        userViewModel.getUser(userId)
        jobsViewModel.getUserJobs(userId)
        postViewModel.setPagingSource(userId)
    }

    private fun setupVisibility() {
        binding.apply {
            if (userViewModel.whosePage(userId)) {
                backButton.visibility = View.GONE
            } else {
                menuButton.visibility = View.GONE
                navButtonGroup.visibility = View.GONE
                addPost.visibility = View.GONE
            }
        }
    }

    private fun setupUserData(user: User) {
        binding.apply {
            if (user.avatar != null) {
                Glide.with(avatar)
                    .load(user.avatar)
                    .placeholder(R.drawable.outline_account_circle_24)
                    .error(R.drawable.outline_error_outline_24)
                    .timeout(10_000)
                    .apply(RequestOptions().circleCrop())
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.outline_account_circle_24)
            }
            name.text = user.name
        }
    }

    private fun initAdapter() {
        adapter = PostAdapter(object : PostListener {
            override fun addObserver(mediaObserver: MediaLifecycleObserver) {
                lifecycle.addObserver(mediaObserver)
            }

            override fun removeObserver(mediaObserver: MediaLifecycleObserver) {
                lifecycle.removeObserver(mediaObserver)
            }

            override fun likeById(post: Post) {
                postViewModel.likeById(post)
            }

            override fun goToUserProfile(id: Int) {
                val action = UserPageFragmentDirections.userPageFragmentToUserPageFragment(id)
                findNavController().navigate(action)
            }

            override fun onRemove(id: Int) {
                postViewModel.onRemove(id)
            }

            override fun onEdit(post: Post) {
                val action = UserPageFragmentDirections.userPageFragmentToNewPostFragment(post)
                findNavController().navigate(action)
            }

            override fun openMedia(attachment: Attachment) {
                val action = UserPageFragmentDirections.userPageFragmentToMediaFragment(attachment)
                findNavController().navigate(action)
            }

        })
        binding.wallList.adapter = adapter
    }

    private fun setupObserver() {
        userViewModel.user.observe(viewLifecycleOwner) { thisUser ->
            if (thisUser != null) {
                setupUserData(thisUser)
            }
        }
        jobsViewModel.userJobs.observe(viewLifecycleOwner) { thisJobs ->
            binding.apply {
                if (thisJobs != null && thisJobs.isNotEmpty()) {
                    jobView.visibility = View.VISIBLE
                    job.visibility = View.VISIBLE
                    jobView.jobList = jobsViewModel.getFilteredList(thisJobs)
                    val jobText =
                        "${
                            jobsViewModel.getFilteredList(thisJobs).lastOrNull()?.position
                        } - ${jobsViewModel.getFilteredList(thisJobs).lastOrNull()?.name}"
                    job.text = jobText
                } else {
                    jobView.visibility = View.GONE
                    job.visibility = View.GONE
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            postViewModel.userWallData.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            postButton.setOnClickListener {
                findNavController().navigate(R.id.userPageFragmentToPostsWallFragment)
            }
            eventButton.setOnClickListener {
                findNavController().navigate(R.id.userPageFragmentToEventPageFragment)
            }
            myProfileButton.setOnClickListener {
                binding.wallList.scrollToPosition(0)
                binding.scrollView.scrollTo(binding.avatar.scrollX, binding.avatar.scrollY)
            }
            addPost.setOnClickListener {
                findNavController().navigate(R.id.userPageFragmentToNewPostFragment)
            }
            menuButton.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_profile)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.editJob -> {
                                val action = UserPageFragmentDirections.userPageFragmentToPlaceToWorkFragment(true)
                                findNavController().navigate(action)
                                true
                            }
                            R.id.logOut -> {
                                userViewModel.logOut()
                                findNavController().navigate(R.id.userPageFragmentToWelcomeFragment)
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
    }


}