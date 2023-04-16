package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nework.apiService.PostApiService
import ru.netology.nework.dataBase.AppDb
import ru.netology.nework.dataBase.dao.PostDao
import ru.netology.nework.dataBase.dao.PostRemoteKeyDao
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.PostRemoteKeyEntity
import ru.netology.nework.error.AppError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val postResponse = when (loadType) {
                LoadType.REFRESH -> {
                    postApiService.getLatest(state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    postApiService.getAfter(id.toString(), state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postApiService.getBefore(id.toString(), state.config.pageSize)
                }
            }

            if (!postResponse.isSuccessful) {
                throw HttpException(postResponse)
            }

            val posts = postResponse.body() ?: throw AppError.ApiError(
                postResponse.code(),
                postResponse.message()
            )

            appDb.withTransaction {

                when (loadType) {
                    LoadType.REFRESH -> {
                        postDao.clear()

                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    posts.first().id
                                ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    posts.last().id
                                )
                            )
                        )
                    }
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                posts.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                posts.last().id
                            )
                        )
                    }
                }
                postDao.insert(posts.map { PostEntity.fromDto(it) })
            }

            return MediatorResult.Success(posts.isEmpty())

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}