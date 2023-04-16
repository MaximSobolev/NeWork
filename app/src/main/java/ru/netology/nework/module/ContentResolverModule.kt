package ru.netology.nework.module

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ContentResolverModule {
    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext
        context: Context
    ) : ContentResolver = context.contentResolver
}