package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import ru.netology.nework.dto.User

interface UserRepository {
    val data : LiveData<List<User>>

    suspend fun requestUsers()
    suspend fun getUsers(ids : List<Int>) : List<User>
    suspend fun getUser(id : Int) : User
}