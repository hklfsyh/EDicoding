package com.example.edicoding.di

import android.content.Context
import com.example.edicoding.api.RetrofitClient
import com.example.edicoding.database.FavoriteEventDatabase
import com.example.edicoding.repository.EventRepository
import com.example.edicoding.repository.FavoriteEventRepository

object AppModule {

    fun provideEventRepository(): EventRepository {
        val apiService = RetrofitClient.apiService
        return EventRepository(apiService)
    }

    fun provideFavoriteEventRepository(context: Context): FavoriteEventRepository {
        val favoriteEventDao = FavoriteEventDatabase.getDatabase(context).favoriteEventDao()
        return FavoriteEventRepository(favoriteEventDao)
    }
}