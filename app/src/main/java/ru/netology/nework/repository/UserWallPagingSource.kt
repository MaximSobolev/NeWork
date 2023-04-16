package ru.netology.nework.repository

import androidx.paging.*
import retrofit2.HttpException
import ru.netology.nework.apiService.PostApiService
import ru.netology.nework.dto.Post
import java.io.IOException

class UserWallPagingSource(
    private val postApiService: PostApiService,
    private val userId: Int
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> postApiService.getUserWallLatest(
                    userId.toString(),
                    params.loadSize
                )
                is LoadParams.Append -> postApiService.getUserWallBefore(
                    userId.toString(),
                    params.key.toString(),
                    params.loadSize
                )
                is LoadParams.Prepend -> postApiService.getUserWallAfter(
                    userId.toString(),
                    params.key.toString(),
                    params.loadSize
                )
            }
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val data = response.body().orEmpty()
            when(params) {
                is LoadParams.Refresh, is LoadParams.Append -> {
                    return LoadResult.Page(
                        data = data,
                        prevKey = params.key,
                        nextKey = data.lastOrNull()?.id
                    )
                }
                is LoadParams.Prepend -> {
                    return LoadResult.Page(
                        data = data,
                        prevKey = data.firstOrNull()?.id,
                        nextKey = params.key
                    )
                }
            }
        } catch (e : IOException) {
            return LoadResult.Error(e)
        }
    }
}