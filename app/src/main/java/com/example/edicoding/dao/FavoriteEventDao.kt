package com.example.edicoding.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.edicoding.model.FavoriteEvent

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Delete
    suspend fun delete(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM FavoriteEvent WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>

    @Query("SELECT * FROM FavoriteEvent")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>>
}