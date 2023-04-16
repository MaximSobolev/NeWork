package ru.netology.nework.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.apiService.PostApiService
import ru.netology.nework.dataBase.AppDb
import ru.netology.nework.dataBase.dao.PostDao
import ru.netology.nework.dataBase.dao.PostRemoteKeyDao
import ru.netology.nework.dto.*
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.error.AppError
import java.io.File
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(postApiService, postDao, postRemoteKeyDao, appDb),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map {
        it.map { postEntity ->
            postEntity.toDto()
        }
    }

    override lateinit var userWallData: Flow<PagingData<Post>>


    override suspend fun save(post : PostCreate) {
        try {
            val attach = if (post.attachment != null) {
                when(post.attachment) {
                    is Attachment -> {
                        post.attachment
                    }
                    is CreateAttachment -> {
                        Attachment(addMedia(post.attachment.file), post.attachment.type)
                    }
                }
            } else null

            val sentPost = post.copy(attachment = attach)

            val response = postApiService.sendPost(sentPost)
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val receivedPost =
                response.body() ?: throw AppError.ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(receivedPost))
        } catch (e: AppError.ApiError) {
            throw e
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun likePost(post: Post) {
        try {
            val response =
                if (post.likedByMe) postApiService.dislikeById(post.id.toString())
                    else postApiService.likeById(post.id.toString())
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val body =
                response.body() ?: throw AppError.ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: AppError.ApiError) {
            throw e
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun onRemove(id: Int) {
        try {
            val response = postApiService.deleteById(id.toString())
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: AppError.ApiError) {
            throw e
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: Exception) {
            throw AppError.UnknownError()
        }
    }

    override fun setNewPager(userId: Int) {
        userWallData = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = {
                UserWallPagingSource(postApiService, userId)
            }
        ).flow
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

}