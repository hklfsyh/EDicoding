// EventViewModel.kt
package com.example.edicoding.viewmodel

import androidx.lifecycle.*
import com.example.edicoding.model.Event
import com.example.edicoding.model.EventResponse
import com.example.edicoding.repository.EventRepository
import com.example.edicoding.util.Resource
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<Resource<List<Event>>>()
    val upcomingEvents: LiveData<Resource<List<Event>>> get() = _upcomingEvents

    private val _finishedEvents = MutableLiveData<Resource<List<Event>>>()
    val finishedEvents: LiveData<Resource<List<Event>>> get() = _finishedEvents

    fun loadUpcomingEvents() {
        _upcomingEvents.value = Resource.Loading()  // Set loading state
        viewModelScope.launch {
            try {
                val events = repository.getUpcomingEvents().listEvents
                _upcomingEvents.value = Resource.Success(events)
            } catch (e: java.net.UnknownHostException) {
                // Kesalahan jaringan
                _upcomingEvents.value = Resource.Error("No Internet Connection")
            } catch (e: Exception) {
                // Kesalahan umum
                _upcomingEvents.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun loadFinishedEvents() {
        _finishedEvents.value = Resource.Loading()  // Set loading state
        viewModelScope.launch {
            try {
                val events = repository.getFinishedEvents().listEvents
                _finishedEvents.value = Resource.Success(events)
            } catch (e: java.net.UnknownHostException) {
                // Kesalahan jaringan
                _finishedEvents.value = Resource.Error("No Internet Connection")
            } catch (e: Exception) {
                _finishedEvents.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun searchEvents(query: String, isUpcoming: Boolean) {
        val call = if (isUpcoming) {
            repository.apiService.searchUpcomingEvents(query = query)
        } else {
            repository.apiService.searchFinishedEvents(query = query)
        }

        // Handle search
        call.enqueue(object : retrofit2.Callback<EventResponse> {
            override fun onResponse(call: retrofit2.Call<EventResponse>, response: retrofit2.Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    if (isUpcoming) {
                        _upcomingEvents.value = Resource.Success(events)
                    } else {
                        _finishedEvents.value = Resource.Success(events)
                    }
                } else {
                    _upcomingEvents.value = Resource.Error("Failed to search events")
                }
            }

            override fun onFailure(call: retrofit2.Call<EventResponse>, t: Throwable) {
                _upcomingEvents.value = Resource.Error("Failed to search events: ${t.message}")
            }
        })
    }
}