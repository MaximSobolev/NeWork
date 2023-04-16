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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventPageBinding
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Event
import ru.netology.nework.media.MediaLifecycleObserver
import ru.netology.nework.recyclerView.event.EventAdapter
import ru.netology.nework.recyclerView.event.EventListener
import ru.netology.nework.viewModel.EventViewModel
import ru.netology.nework.viewModel.UserViewModel

@AndroidEntryPoint
class EventPageFragment : Fragment() {

    private lateinit var binding: FragmentEventPageBinding
    private lateinit var adapter: EventAdapter
    private val eventViewModel: EventViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setupAdapter()
        setupObservers()
        setupListeners()

        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentEventPageBinding.inflate(
            inflater,
            container,
            false
        )
    }

    private fun setupAdapter() {
        adapter = EventAdapter(object : EventListener {
            override fun addObserver(mediaObserver: MediaLifecycleObserver) {
                lifecycle.addObserver(mediaObserver)
            }

            override fun removeObserver(mediaObserver: MediaLifecycleObserver) {
                lifecycle.removeObserver(mediaObserver)
            }

            override fun goToUserProfile(id: Int) {
                val action = EventPageFragmentDirections.eventPageFragmentToUserPageFragment(id)
                findNavController().navigate(action)
            }

            override fun likeEvent(event: Event) {
                eventViewModel.likeEvent(event)
            }

            override fun participateEvent(event: Event) {
                eventViewModel.participateEvent(event)
            }

            override fun onRemove(id: Int) {
                eventViewModel.removeEventById(id)
            }

            override fun onEdit(event: Event) {
                val action = EventPageFragmentDirections.eventPageFragmentToNewEventFragment(event)
                findNavController().navigate(action)
            }

            override fun openMedia(attachment: Attachment) {
                val action = EventPageFragmentDirections.eventPageFragmentToMediaFragment(attachment)
                findNavController().navigate(action)
            }
        }, requireContext())
        binding.list.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenCreated {
            eventViewModel.data.collectLatest {
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
            list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            myProfileButton.setOnClickListener {
                val action =
                    EventPageFragmentDirections.eventPageFragmentToUserPageFragment(userViewModel.getMyId())
                findNavController().navigate(action)
            }
            postButton.setOnClickListener {
                findNavController().navigate(R.id.eventPageFragmentToPostsWallFragment)
            }
            eventButton.setOnClickListener {
                list.scrollToPosition(0)
            }
            addEvent.setOnClickListener {
                findNavController().navigate(R.id.eventPageFragmentToNewEventFragment)
            }

        }
    }

}