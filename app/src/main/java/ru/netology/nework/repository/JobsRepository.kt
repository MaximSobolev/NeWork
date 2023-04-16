package ru.netology.nework.repository

import ru.netology.nework.dto.Job

interface JobsRepository {
    suspend fun save(job : Job) : Job
    suspend fun getUserJob(userId : Int) : List<Job>
    suspend fun deleteJob(jobId : Int)
}