package com.example.edicoding.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("listEvents")
	val listEvents: List<Event>
)
