package ru.netology.nework.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.dataBase.AppDb
import ru.netology.nework.dataBase.dao.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppDbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .build()

    @Singleton
    @Provides
    fun providePostDao(
        appDb: AppDb
    ) : PostDao = appDb.postDao()

    @Singleton
    @Provides
    fun providePostRemoteKeyDao (
        appDb: AppDb
    ) : PostRemoteKeyDao = appDb.postRemoteKeyDao()

    @Singleton
    @Provides
    fun provideEventDao (
        appDb: AppDb
    ) : EventDao = appDb.eventDao()

    @Singleton
    @Provides
    fun provideEventRemoteKeyDao(
        appDb: AppDb
    ) : EventRemoteKeyDao = appDb.eventRemoteKeyDao()

    @Singleton
    @Provides
    fun provideUserDao(
        appDb: AppDb
    ) : UserDao = appDb.userDao()
}