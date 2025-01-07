package com.example.edicoding.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edicoding.R
import com.example.edicoding.model.Event

class EventAdapter(
    private var eventList: List<Event>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventNameTextView.text = event.name

        Glide.with(holder.itemView.context)
            .load(event.imageLogo)
            .into(holder.eventImageView)

        holder.itemView.setOnClickListener {
            onItemClick(event.id)
        }
    }

    override fun getItemCount(): Int = eventList.size

    fun updateEvents(newEvents: List<Event>) {
        eventList = newEvents
        notifyDataSetChanged()
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventImageView: ImageView = view.findViewById(R.id.eventImageView)
        val eventNameTextView: TextView = view.findViewById(R.id.eventNameTextView)
    }
}
