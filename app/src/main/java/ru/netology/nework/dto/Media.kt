package ru.netology.nework.dto

import java.io.File

sealed class Media

data class Attachment(
    val url: String,
    val type: AttachType
) : Media(), java.io.Serializable

data class CreateAttachment (
    val file : File,
    val type : AttachType
) : Media()