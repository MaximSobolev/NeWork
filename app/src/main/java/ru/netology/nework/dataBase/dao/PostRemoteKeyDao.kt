package ru.netology.nework.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {
    @Query("SELECT MAX(`key`) FROM PostRemoteKeyEntity")
    suspend fun max() : Int?

    @Query("SELECT MIN(`key`) FROM PostRemoteKeyEntity")
    suspend fun min() : Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key : PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun clear()
}