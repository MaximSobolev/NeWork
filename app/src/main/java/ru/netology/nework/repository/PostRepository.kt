package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.PostCreate
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    var userWallData : Flow<PagingData<Post>>
    suspend fun save(post : PostCreate)
    suspend fun likePost(post : Post)
    suspend fun onRemove(id: Int)
    fun setNewPager(userId : Int)
    suspend fun addMedia(file: File): String
}