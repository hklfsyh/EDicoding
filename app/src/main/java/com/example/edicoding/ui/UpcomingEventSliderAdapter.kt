package com.example.edicoding.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.edicoding.R
import com.example.edicoding.model.Event

class UpcomingEventSliderAdapter(
    private var events: List<Event>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<UpcomingEventSliderAdapter.EventViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEventList: List<Event>) {
        events = newEventList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = events[position]

        holder.eventNameTextView.text = currentEvent.name

        Glide.with(holder.itemView.context)
            .load(currentEvent.mediaCover)
            .placeholder(R.drawable.eplplaceholder)
            .error(R.drawable.eplplaceholder)
            .into(holder.eventImageView)

        holder.itemView.setOnClickListener {
            onItemClick(currentEvent.id)
        }
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImageView: ImageView = itemView.findViewById(R.id.eventImageView)
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
    }
}
