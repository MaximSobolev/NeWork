package ru.netology.nework.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dataBase.dao.*
import ru.netology.nework.entity.*

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class, EventEntity::class, EventRemoteKeyEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao() : EventDao
    abstract fun eventRemoteKeyDao() : EventRemoteKeyDao
    abstract fun userDao() : UserDao
}