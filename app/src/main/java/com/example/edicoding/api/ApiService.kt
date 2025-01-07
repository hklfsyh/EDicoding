package com.example.edicoding.api

import com.example.edicoding.model.DetailEventResponse
import com.example.edicoding.model.EventResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvents(): EventResponse

    @GET("events?active=0")
    suspend fun getFinishedEvents(): EventResponse

    @GET("events")
    fun searchUpcomingEvents(
        @Query("active") active: Int = 1,
        @Query("q") query: String
    ): Call<EventResponse>

    @GET("events")
    fun searchFinishedEvents(
        @Query("active") active: Int = 0,
        @Query("q") query: String
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: String): Call<DetailEventResponse>

    @GET("events")
    suspend fun getNearestActiveEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): Response<EventResponse>
}

