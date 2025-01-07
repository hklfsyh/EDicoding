package com.example.edicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.edicoding.api.RetrofitClient
import com.example.edicoding.model.DetailEvent
import com.example.edicoding.model.DetailEventResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventDetailViewModel : ViewModel() {
    private val _detailEvent = MutableLiveData<DetailEvent?>()
    val detailEvent: LiveData<DetailEvent?> get() = _detailEvent

    private val apiService = RetrofitClient.apiService

    fun loadEventDetail(eventId: String) {
        apiService.getDetailEvent(eventId).enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.event?.let { event ->
                        _detailEvent.postValue(event)
                    } ?: run {
                        _detailEvent.postValue(null)
                    }
                } else {
                    _detailEvent.postValue(null)
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _detailEvent.postValue(null)
            }
        })
    }
}
