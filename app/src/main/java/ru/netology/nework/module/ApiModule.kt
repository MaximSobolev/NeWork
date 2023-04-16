package ru.netology.nework.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nework.apiService.EventApiService
import ru.netology.nework.apiService.JobsApiService
import ru.netology.nework.apiService.PostApiService
import ru.netology.nework.apiService.UserApiService
import ru.netology.nework.authentication.AppAuth
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    companion object {
        private const val BASE_URL = "https://netomedia.ru/"
    }

    @Provides
    @Singleton
    fun provideLogging() : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        appAuth: AppAuth,
        logging : HttpLoggingInterceptor
    ) : OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor {chain ->
            appAuth.authStateFlow.value.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .build()
                return@addInterceptor chain.proceed(newRequest)
            } ?: return@addInterceptor chain.proceed(chain.request())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideUserService(
        retrofit: Retrofit
    ) : UserApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideJobsService(
        retrofit: Retrofit
    ) : JobsApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePostService(
        retrofit: Retrofit
    ) : PostApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideEventService(
        retrofit: Retrofit
    ) : EventApiService = retrofit.create()



}