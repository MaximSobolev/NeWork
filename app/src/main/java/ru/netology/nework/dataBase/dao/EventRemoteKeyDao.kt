package ru.netology.nework.dataBase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nework.entity.EventRemoteKeyEntity

@Dao
interface EventRemoteKeyDao {
    @Query("SELECT MAX(`key`) FROM EventRemoteKeyEntity")
    suspend fun max() : Int?

    @Query("SELECT MIN(`key`) FROM EventRemoteKeyEntity")
    suspend fun min() : Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key : EventRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<EventRemoteKeyEntity>)

    @Query("DELETE FROM EventRemoteKeyEntity")
    suspend fun clear()
}