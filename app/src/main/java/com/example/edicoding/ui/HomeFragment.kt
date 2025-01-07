package com.example.edicoding.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edicoding.di.AppModule
import com.example.edicoding.util.Resource
import com.example.edicoding.viewmodel.EventViewModel
import com.example.edicoding.viewmodel.EventViewModelFactory
import com.example.edicoding.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var finishedEventAdapter: EventAdapter
    private lateinit var upcomingEventSliderAdapter: UpcomingEventSliderAdapter

    @Suppress("DEPRECATION")
    private val handler = Handler()
    private lateinit var autoScrollRunnable: Runnable

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(AppModule.provideEventRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.upcomingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.finishedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        upcomingEventSliderAdapter = UpcomingEventSliderAdapter(emptyList()) { eventId ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
        }
        binding.upcomingRecyclerView.adapter = upcomingEventSliderAdapter

        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val limitedUpcomingEvents = resource.data?.take(5) ?: emptyList()
                    upcomingEventSliderAdapter.updateEvents(limitedUpcomingEvents)
                    hideLoadingAndError()
                }

                is Resource.Error -> {
                    showError(resource.message ?: "An unknown error occurred")
                }
            }
        }

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Success -> {
                    val limitedFinishedEvents = resource.data?.take(5) ?: emptyList()
                    finishedEventAdapter = EventAdapter(limitedFinishedEvents) { eventId ->
                        val intent = Intent(requireContext(), EventDetailActivity::class.java)
                        intent.putExtra("EVENT_ID", eventId)
                        startActivity(intent)
                    }
                    binding.finishedRecyclerView.adapter = finishedEventAdapter
                    hideLoadingAndError()
                }

                is Resource.Error -> {
                    showError(resource.message ?: "An unknown error occurred")
                }
            }
        }

        showLoading()
        eventViewModel.loadUpcomingEvents()
        eventViewModel.loadFinishedEvents()

        autoScrollRunnable = Runnable {
            val layoutManager = binding.upcomingRecyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            val itemCount = upcomingEventSliderAdapter.itemCount

            if (firstVisiblePosition < itemCount - 1) {
                binding.upcomingRecyclerView.smoothScrollToPosition(firstVisiblePosition + 1)
            } else {
                binding.upcomingRecyclerView.smoothScrollToPosition(0)
            }

            handler.postDelayed(autoScrollRunnable, 2000)
        }

        return view
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.upcomingRecyclerView.visibility = View.GONE
        binding.finishedRecyclerView.visibility = View.GONE
    }

    private fun hideLoadingAndError() {
        binding.progressBar.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
        binding.upcomingRecyclerView.visibility = View.VISIBLE
        binding.finishedRecyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorMessage.text =
            if (message.contains("No Internet Connection", ignoreCase = true)) {
                "No Internet Connection. Please check your connection."
            } else {
                message
            }

        binding.refreshButton.setOnClickListener {
            refreshData()
        }
        binding.refreshButton.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.VISIBLE
        binding.upcomingRecyclerView.visibility = View.GONE
        binding.finishedRecyclerView.visibility = View.GONE
    }

    private fun refreshData() {
        showLoading()
        eventViewModel.loadUpcomingEvents()
        eventViewModel.loadFinishedEvents()
    }

    override fun onResume() {
        super.onResume()
        handler.post(autoScrollRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
