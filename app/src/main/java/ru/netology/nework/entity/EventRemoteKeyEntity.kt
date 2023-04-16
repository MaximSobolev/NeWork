package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EventRemoteKeyEntity (
    @PrimaryKey
    val type: PostRemoteKeyEntity.KeyType = PostRemoteKeyEntity.KeyType.AFTER,
    val key: Int = 0
)