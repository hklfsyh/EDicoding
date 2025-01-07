package com.example.edicoding.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edicoding.databinding.FragmentFavoriteBinding
import com.example.edicoding.model.Event
import com.example.edicoding.viewmodel.FavoriteEventViewModel
import com.example.edicoding.viewmodel.FavoriteEventViewModelFactory
import com.example.edicoding.di.AppModule

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter
    private val favoriteEventViewModel: FavoriteEventViewModel by viewModels {
        FavoriteEventViewModelFactory(AppModule.provideFavoriteEventRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(emptyList()) { eventId ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
        }

        binding.favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }

        favoriteEventViewModel.getAllFavoriteEvents()
            .observe(viewLifecycleOwner) { favoriteEvents ->
                // Map FavoriteEvent ke Event
                val eventList = favoriteEvents.map { favoriteEvent ->
                    Event(
                        id = favoriteEvent.id.toInt(),
                        name = favoriteEvent.name,
                        summary = "",
                        description = "",
                        imageLogo = favoriteEvent.mediaCover ?: "",
                        mediaCover = favoriteEvent.mediaCover ?: "",
                        category = "",
                        ownerName = "",
                        cityName = "",
                        quota = 0,
                        registrants = 0,
                        beginTime = "",
                        endTime = "",
                        link = ""
                    )
                }
                eventAdapter.updateEvents(eventList)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
