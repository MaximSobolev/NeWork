package ru.netology.nework.repository

import ru.netology.nework.apiService.JobsApiService
import ru.netology.nework.dto.Job
import ru.netology.nework.error.AppError
import java.io.IOException
import javax.inject.Inject

class JobsRepositoryImpl @Inject constructor(
    private val jobsApiService: JobsApiService,
) : JobsRepository {

    override suspend fun save(job: Job) : Job {
        try {
            val response = jobsApiService.save(job)
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

    override suspend fun getUserJob(userId: Int): List<Job> {
        try {
            val response = jobsApiService.getUserJob(userId)
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

    override suspend fun deleteJob(jobId: Int) {
        try {
            val response = jobsApiService.deleteJob(jobId.toString())
            if (!response.isSuccessful) {
                throw AppError.ApiError(response.code(), response.message())
            }
        } catch (e : AppError) {
            throw e
        } catch (e : IOException) {
            throw AppError.NetworkError()
        } catch (e : Exception) {
            throw AppError.UnknownError()
        }

    }
}