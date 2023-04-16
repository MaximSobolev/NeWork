package ru.netology.nework.repository

import ru.netology.nework.model.AuthState

interface SignInRepository {
    suspend fun signIn(login: String, password: String) : AuthState
}