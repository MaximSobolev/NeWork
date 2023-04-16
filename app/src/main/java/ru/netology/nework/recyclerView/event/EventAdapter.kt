package ru.netology.nework.recyclerView.event

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import ru.netology.nework.databinding.CardEventBinding
import ru.netology.nework.dto.Event

class EventAdapter (
    private val eventListener: EventListener,
    private val context : Context
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        payloads.forEach {
            (it as? EventPayload)?.let {payload ->
                val event = getItem(position) ?: return
                holder.bind(payload, event)
            }
        }
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, eventListener, context)
    }

}