package ru.netology.nework.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.apiService.EventApiService
import ru.netology.nework.apiService.PostApiService
import ru.netology.nework.dataBase.AppDb
import ru.netology.nework.dataBase.dao.EventDao
import ru.netology.nework.dataBase.dao.EventRemoteKeyDao
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.CreateAttachment
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventCreate
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.error.AppError
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    private val postApiService: PostApiService,
    private val eventDao: EventDao,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = EventRemoteMediator(
            eventApiService,
            eventDao,
            eventRemoteKeyDao,
            appDb
        ),
        pagingSourceFactory = eventDao::pagingSource
    ).flow.map {
        it.map { entity ->
            entity.toDto()
        }
    }

    override suspend fun save(event: EventCreate) {
        try {
            val attach = if (event.attachment != null) {
                when(event.attachment) {
                    is Attachment -> {
                        event.attachment
                    }
                    is CreateAttachment -> {
                        Attachment(addMedia(event.attachment.file), event.attachment.type)
                    }
                }
            } else null

            val sentEvent = event.copy(attachment = attach)

            val response = eventApiService.sendEvent(sentEvent)
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val receivedEvent =
                response.body() ?: throw AppError.ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(receivedEvent))
        } catch (e: AppError.ApiError) {
            throw e
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun addMedia(file: File): String {
        try {
            val response = postApiService.addMedia(
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    file.asRequestBody()
                )
            )
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val body =
                response.body() ?: throw AppError.ApiError(response.code(), response.message())
            return body.url
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun likeEvent(event: Event) {
        try {
            val response = if (event.likedByMe) {
                eventApiService.dislikeEvent(event.id.toString())
            } else {
                eventApiService.likeEvent(event.id.toString())
            }
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw AppError.ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun participateEvent(event: Event) {
        try {
            val response = if (event.participatedByMe) {
                eventApiService.cancelParticipateEvent(event.id.toString())
            } else {
                eventApiService.participateEvent(event.id.toString())
            }
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw AppError.ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun removeEventById(id: Int) {
        try {
            val response = eventApiService.deleteEvent(id.toString())
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            eventDao.removeById(id)
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }

}