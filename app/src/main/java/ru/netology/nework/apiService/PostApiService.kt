package ru.netology.nework.apiService

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.MediaResponse
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.PostCreate


interface PostApiService {

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: String, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: String, @Query("count") count: Int): Response<List<Post>>

    @GET("api/{authorId}/wall/latest/")
    suspend fun getUserWallLatest(
        @Path("authorId") authorId: String,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{postId}/after/")
    suspend fun getUserWallAfter(
        @Path("authorId") authorId: String,
        @Path("postId") postId: String,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/{authorId}/wall/{postId}/before/")
    suspend fun getUserWallBefore(
        @Path("authorId") authorId: String,
        @Path("postId") postId: String,
        @Query("count") count: Int
    ): Response<List<Post>>

    @Multipart
    @POST("api/media/")
    suspend fun addMedia(@Part media: MultipartBody.Part): Response<MediaResponse>

    @POST("api/posts")
    suspend fun sendPost(@Body post: PostCreate): Response<Post>

    @DELETE("api/posts/{id}/")
    suspend fun deleteById(@Path("id") id: String): Response<Unit>

    @POST("api/posts/{id}/likes/")
    suspend fun likeById(@Path("id") id: String): Response<Post>

    @DELETE("api/posts/{id}/likes/")
    suspend fun dislikeById(@Path("id") id: String): Response<Post>


}