package com.example.edicoding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edicoding.repository.FavoriteEventRepository

class FavoriteEventViewModelFactory(private val repository: FavoriteEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}