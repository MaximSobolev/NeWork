package ru.netology.nework.apiService

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nework.dto.Job

interface JobsApiService {

    @POST("api/my/jobs/")
    suspend fun save(@Body jobs: Job) : Response<Job>

    @GET("api/{userId}/jobs/")
    suspend fun getUserJob(@Path("userId") userId : Int) : Response<List<Job>>

    @DELETE("api/my/jobs/{jobId}/")
    suspend fun deleteJob(@Path("jobId") jobId : String) : Response<Unit>

}