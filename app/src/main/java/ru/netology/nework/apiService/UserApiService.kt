package ru.netology.nework.apiService

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.AuthRequest
import ru.netology.nework.dto.User
import ru.netology.nework.model.AuthState

interface UserApiService {
    @POST("api/users/authentication/")
    suspend fun signIn(
        @Body authRequest: AuthRequest
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("api/users/registration/")
    suspend fun signUp(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ) : Response<AuthState>

    @Multipart
    @POST("api/users/registration/")
    suspend fun signUpWithAvatar(
        @Part("login") login : RequestBody,
        @Part("password") pass : RequestBody,
        @Part("name") name : RequestBody,
        @Part media : MultipartBody.Part
    ) : Response<AuthState>

    @GET("api/users")
    suspend fun getAll() : Response<List<User>>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id : Int) : Response<User>
}