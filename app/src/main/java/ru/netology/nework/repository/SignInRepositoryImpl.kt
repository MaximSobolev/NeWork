package ru.netology.nework.repository

import ru.netology.nework.apiService.UserApiService
import ru.netology.nework.dto.AuthRequest
import ru.netology.nework.error.AppError
import ru.netology.nework.model.AuthState
import java.io.IOException
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val userService : UserApiService
) : SignInRepository {

    override suspend fun signIn(login: String, password: String) : AuthState {
        try {
            val response = userService.signIn(AuthRequest(login, password))
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