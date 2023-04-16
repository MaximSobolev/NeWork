package ru.netology.nework.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nework.apiService.UserApiService
import ru.netology.nework.error.AppError
import ru.netology.nework.model.AuthState
import java.io.File
import java.io.IOException
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val userService: UserApiService
) : SignUpRepository {

    override suspend fun signUp(
        login: String,
        password: String,
        name: String,
    ) : AuthState {
        try {
            val response = userService.signUp(login, password, name)
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }

            return response.body() ?: throw AppError.ApiError(response.code(), response.message())
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun signUpWithAvatar(
        login: String,
        password: String,
        name: String,
        avatar: File
    ) : AuthState {
        try {
            val response =
                userService.signUpWithAvatar(
                    login.toRequestBody("text/plain".toMediaType()),
                    password.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                    MultipartBody.Part.createFormData(
                        "file",
                        avatar.name,
                        avatar.asRequestBody()
                ))
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            return response.body() ?: throw AppError.ApiError(response.code(), response.message())
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }
}