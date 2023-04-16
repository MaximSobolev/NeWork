package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: KeyType = KeyType.AFTER,
    val key: Int = 0
) {
    enum class KeyType {
        AFTER,
        BEFORE
    }
}