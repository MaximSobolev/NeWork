package ru.netology.nework.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostsWallBinding
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Post
import ru.netology.nework.media.MediaLifecycleObserver
import ru.netology.nework.recyclerView.post.PostAdapter
import ru.netology.nework.recyclerView.post.PostListener
import ru.netology.nework.viewModel.PostViewModel
import ru.netology.nework.viewModel.UserViewModel

@AndroidEntryPoint
class PostsWallFragment : Fragment() {
    private lateinit var binding: FragmentPostsWallBinding
    private lateinit var adapter: PostAdapter
    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        initAdapter()
        setupObserver()
        setupListeners()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentPostsWallBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun initAdapter() {
        adapter = PostAdapter(
            object : PostListener {
                override fun addObserver(mediaObserver: MediaLifecycleObserver) {
                    lifecycle.addObserver(mediaObserver)
                }

                override fun removeObserver(mediaObserver: MediaLifecycleObserver) {
                    lifecycle.removeObserver(mediaObserver)
                }

                override fun likeById(post: Post) {
                    viewModel.likeById(post)
                }

                override fun goToUserProfile(id: Int) {
                    val action = PostsWallFragmentDirections.postsWallFragmentToUserPageFragment(id)
                    findNavController().navigate(action)
                }

                override fun onRemove(id: Int) {
                    viewModel.onRemove(id)
                }

                override fun onEdit(post: Post) {
                    val action =
                        PostsWallFragmentDirections.postsWallFragmentToNewPostFragment(post)
                    findNavController().navigate(action)
                }

                override fun openMedia(attachment: Attachment) {
                    val action = PostsWallFragmentDirections.postsWallFragmentToMediaFragment(attachment)
                    findNavController().navigate(action)
                }
            }
        )
        binding.list.adapter = adapter
    }

    private fun setupObserver() {
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
            }
        }
    }

    private fun setupListeners() {
        binding.apply {

            list.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val topVerticalPosition = if (recyclerView.childCount == 0) {
                        0
                    } else {
                        recyclerView.getChildAt(0).top
                    }
                    swipeRefreshLayout.isEnabled = topVerticalPosition >= 0
                }
            })
            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }
            addPost.setOnClickListener {
                findNavController().navigate(R.id.postsWallFragmentToNewPostFragment)
            }
            postButton.setOnClickListener {
                list.scrollToPosition(0)
            }
            myProfileButton.setOnClickListener {
                val action =
                    PostsWallFragmentDirections.postsWallFragmentToUserPageFragment(userViewModel.getMyId())
                findNavController().navigate(action)
            }
            eventButton.setOnClickListener {
                findNavController().navigate(R.id.postsWallFragmentToEventPageFragment)
            }
        }
    }

}