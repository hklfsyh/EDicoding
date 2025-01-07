package com.example.edicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edicoding.model.FavoriteEvent
import com.example.edicoding.repository.FavoriteEventRepository
import kotlinx.coroutines.launch

class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent> {
        return repository.getFavoriteEventById(id) // Mengambil event favorit berdasarkan ID
    }

    fun insert(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            repository.insert(favoriteEvent) // Menyisipkan event favorit
        }
    }

    fun delete(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            repository.delete(favoriteEvent) // Menghapus event favorit
        }
    }

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return repository.getAllFavoriteEvents() // Mengambil semua event favorit
    }
}