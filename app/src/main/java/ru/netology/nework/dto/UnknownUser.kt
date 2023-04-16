package ru.netology.nework.dto

import java.io.File

data class UnknownUser (
    val login : String = "",
    val password : String = "",
    val name : String = "",
    val avatar : File? = null
        )