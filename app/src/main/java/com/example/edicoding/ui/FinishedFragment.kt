package com.example.edicoding.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.edicoding.R
import com.example.edicoding.di.AppModule
import com.example.edicoding.util.Resource
import com.example.edicoding.viewmodel.EventViewModel
import com.example.edicoding.viewmodel.EventViewModelFactory
import com.example.edicoding.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(AppModule.provideEventRepository())
    }

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.finishedRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        finishedEventAdapter = EventAdapter(emptyList()) { eventId ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra("EVENT_ID", eventId)
            startActivity(intent)
        }
        binding.finishedRecyclerView.adapter = finishedEventAdapter

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    resource.data?.let { events ->
                        finishedEventAdapter.updateEvents(events)
                        binding.noResultsTextView.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
                        binding.refreshButton.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
                        binding.finishedRecyclerView.visibility = View.VISIBLE
                    }
                    hideLoadingAndError()
                }
                is Resource.Error -> {
                    showError(resource.message ?: "An unknown error occurred")
                }
            }
        }

        if (eventViewModel.finishedEvents.value == null) {
            showLoading()
            eventViewModel.loadFinishedEvents()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    eventViewModel.searchEvents(it, false)
                    hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    eventViewModel.searchEvents(it, false)
                }
                return true
            }
        })

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.refreshButton.setOnClickListener {
            resetSearch()
        }

        return view
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.finishedRecyclerView.visibility = View.GONE
    }

    private fun hideLoadingAndError() {
        binding.progressBar.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
        binding.finishedRecyclerView.visibility = View.VISIBLE
        binding.refreshButton2.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorMessage.text = message

        if (message.contains("No Internet Connection", ignoreCase = true)) {
            binding.errorMessage.text =
                getString(R.string.no_internet_connection_please_check_your_connection)
        } else {
            binding.errorMessage.text = message
        }

        binding.refreshButton2.setOnClickListener {
            refreshData()
        }
        binding.refreshButton2.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.VISIBLE
        binding.finishedRecyclerView.visibility = View.GONE
    }

    private fun refreshData() {
        showLoading()
        eventViewModel.loadFinishedEvents()
    }

    private fun resetSearch() {
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        hideKeyboard()

        eventViewModel.loadFinishedEvents()
        binding.noResultsTextView.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}