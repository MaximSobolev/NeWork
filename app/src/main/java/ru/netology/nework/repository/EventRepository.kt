package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventCreate
import java.io.File

interface EventRepository {
    val data : Flow<PagingData<Event>>
    suspend fun save(event : EventCreate)
    suspend fun likeEvent(event : Event)
    suspend fun participateEvent(event : Event)
    suspend fun removeEventById(id: Int)
    suspend fun addMedia(file: File): String
}