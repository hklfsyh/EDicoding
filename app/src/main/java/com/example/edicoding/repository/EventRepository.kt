package com.example.edicoding.repository

import com.example.edicoding.api.ApiService
import com.example.edicoding.model.EventResponse

class EventRepository(val apiService: ApiService) {

    suspend fun getUpcomingEvents(): EventResponse {
        return apiService.getUpcomingEvents()
    }

    suspend fun getFinishedEvents(): EventResponse {
        return apiService.getFinishedEvents()
    }
}