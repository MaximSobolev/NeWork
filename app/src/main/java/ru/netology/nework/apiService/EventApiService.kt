package ru.netology.nework.apiService

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventCreate

interface EventApiService {

    @GET("api/events/latest/")
    suspend fun getLatest(@Query("count") count : Int) : Response<List<Event>>

    @GET("api/events/{id}/after/")
    suspend fun getAfter(@Path("id") id : String, @Query("count") count : Int) : Response<List<Event>>

    @GET("api/events/{id}/before/")
    suspend fun getBefore(@Path("id") id : String, @Query("count") count : Int) : Response<List<Event>>

    @POST("api/events/")
    suspend fun sendEvent(@Body event : EventCreate) : Response<Event>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id : String) : Response<Unit>

    @POST("api/events/{id}/likes/")
    suspend fun likeEvent(@Path("id") id : String) : Response<Event>

    @DELETE("api/events/{id}/likes/")
    suspend fun dislikeEvent(@Path("id") id : String) : Response<Event>

    @POST("api/events/{id}/participants/")
    suspend fun participateEvent(@Path("id") id : String) : Response<Event>

    @DELETE("api/events/{id}/participants/")
    suspend fun cancelParticipateEvent(@Path("id") id : String) : Response<Event>

}