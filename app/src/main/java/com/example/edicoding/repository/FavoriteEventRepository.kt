package com.example.edicoding.repository

import androidx.lifecycle.LiveData
import com.example.edicoding.dao.FavoriteEventDao
import com.example.edicoding.model.FavoriteEvent

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    suspend fun insert(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.insert(favoriteEvent)
    }

    suspend fun delete(favoriteEvent: FavoriteEvent) {
        favoriteEventDao.delete(favoriteEvent)
    }

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavoriteEvents()
    }
}