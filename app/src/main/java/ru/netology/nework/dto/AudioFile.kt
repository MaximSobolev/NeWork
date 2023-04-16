package ru.netology.nework.dto

import android.net.Uri
import java.io.File

data class AudioFile(
    val uri : Uri,
    val name : String,
    val duration: Int,
    val file : File? = null
)