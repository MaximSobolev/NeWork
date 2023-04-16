package ru.netology.nework.repository

import ru.netology.nework.model.AuthState
import java.io.File

interface SignUpRepository {
    suspend fun signUp(
        login: String,
        password: String,
        name: String,
    ) : AuthState

    suspend fun signUpWithAvatar(
        login: String,
        password: String,
        name: String,
        avatar : File
    ) : AuthState
}