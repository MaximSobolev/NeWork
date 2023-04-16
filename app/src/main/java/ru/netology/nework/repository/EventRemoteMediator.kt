package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.apiService.EventApiService
import ru.netology.nework.dataBase.AppDb
import ru.netology.nework.dataBase.dao.EventDao
import ru.netology.nework.dataBase.dao.EventRemoteKeyDao
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.EventRemoteKeyEntity
import ru.netology.nework.entity.PostRemoteKeyEntity
import ru.netology.nework.error.AppError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb

) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val eventResponse = when (loadType) {
                LoadType.REFRESH -> {
                    eventApiService.getLatest(state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    eventApiService.getAfter(id.toString(), state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    eventApiService.getBefore(id.toString(), state.config.pageSize)
                }
            }
            if (!eventResponse.isSuccessful) {
                throw HttpException(eventResponse)
            }

            val events = eventResponse.body() ?: throw AppError.ApiError(
                eventResponse.code(),
                eventResponse.message()
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventDao.clear()
                        eventRemoteKeyDao.insert(
                            listOf(
                                EventRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    events.first().id
                                ),
                                EventRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    events.last().id
                                )
                            )
                        )
                    }
                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                events.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                events.last().id
                            )
                        )
                    }
                }
                eventDao.insert(events.map { EventEntity.fromDto(it) })
            }
            return MediatorResult.Success(events.isEmpty())
        } catch (e : Exception) {
            return MediatorResult.Error(e)
        }
    }
}