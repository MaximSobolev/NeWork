package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nework.apiService.UserApiService
import ru.netology.nework.dataBase.dao.UserDao
import ru.netology.nework.dto.User
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.error.AppError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
    private val userDao: UserDao
) : UserRepository {

    override val data: LiveData<List<User>> =
        userDao.getAll().map { list -> list.map { it.toDto() } }

    override suspend fun requestUsers() {
        try {
            val response = userApiService.getAll()
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            val body =
                response.body() ?: throw AppError.ApiError(response.code(), response.message())
            userDao.insert(body.map { UserEntity.fromDto(it) })
        } catch (e: AppError.ApiError) {
            throw e
        } catch (e: IOException) {
            throw AppError.NetworkError()
        } catch (e: java.lang.Exception) {
            throw AppError.UnknownError()
        }
    }

    override suspend fun getUsers(ids: List<Int>): List<User> {
        return userDao.getUsersList(ids).map { it.toDto() }
    }

    override suspend fun getUser(id: Int): User {
        try {
            val response = userApiService.getUser(id)
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
            return response.body() ?: throw AppError.ApiError(response.code(), response.message())
        } catch (e : AppError.ApiError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }
    }


}