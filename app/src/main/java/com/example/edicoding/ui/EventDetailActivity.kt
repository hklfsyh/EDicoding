package com.example.edicoding.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.edicoding.model.DetailEvent
import androidx.core.text.HtmlCompat
import com.example.edicoding.viewmodel.EventDetailViewModel
import com.example.edicoding.R
import com.example.edicoding.model.FavoriteEvent
import com.example.edicoding.viewmodel.FavoriteEventViewModel
import com.example.edicoding.viewmodel.FavoriteEventViewModelFactory
import com.example.edicoding.di.AppModule
import com.example.edicoding.databinding.ActivityEventDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventDetailViewModel
    private lateinit var favoriteEventViewModel: FavoriteEventViewModel
    private var eventId: Int = -1
    private lateinit var binding: ActivityEventDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getIntExtra("EVENT_ID", -1)
        eventViewModel = ViewModelProvider(this)[EventDetailViewModel::class.java]
        favoriteEventViewModel = ViewModelProvider(
            this,
            FavoriteEventViewModelFactory(
                AppModule.provideFavoriteEventRepository(
                    applicationContext
                )
            )
        )[FavoriteEventViewModel::class.java]

        showLoading()

        eventViewModel.loadEventDetail(eventId.toString())

        eventViewModel.detailEvent.observe(this) { detailEvent ->
            if (detailEvent != null) {
                populateEventDetail(detailEvent)
                hideLoading()

                favoriteEventViewModel.getFavoriteEventById(eventId.toString())
                    .observe(this) { favoriteEvent ->
                        if (favoriteEvent == null) {
                            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Tampilkan ikon border
                            binding.favoriteButton.colorFilter =
                                null
                            binding.favoriteButton.setOnClickListener {
                                favoriteEventViewModel.insert(
                                    FavoriteEvent(
                                        eventId.toString(),
                                        detailEvent.name,
                                        detailEvent.mediaCover
                                    )
                                )
                            }
                        } else {
                            binding.favoriteButton.setImageResource(R.drawable.ic_favorite) // Tampilkan ikon penuh
                            binding.favoriteButton.setColorFilter(
                                0xFFFF0000.toInt(),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                            binding.favoriteButton.setOnClickListener {
                                favoriteEventViewModel.delete(favoriteEvent)
                            }
                        }
                    }
            } else {
                showError("Failed to load event data details. Please check your internet connection and try again.")
                hideLoading()
            }
        }
    }

    private fun populateEventDetail(detailEvent: DetailEvent) {
        binding.eventNameTextView.text = detailEvent.name
        binding.eventSummaryTextView.text = detailEvent.summary
        binding.eventDescriptionTextView.text = HtmlCompat.fromHtml(
            detailEvent.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(detailEvent.beginTime)
        val formattedDate = date?.let { outputFormat.format(it) }

        binding.eventDateTextView.text = getString(R.string.event_date, formattedDate)
        binding.eventLocationTextView.text = getString(R.string.location, detailEvent.cityName)
        binding.eventOwnerTextView.text = getString(R.string.owner, detailEvent.ownerName)
        binding.eventQuotaTextView.text = getString(R.string.quota, detailEvent.quota)
        binding.eventRegistrantsTextView.text =
            getString(R.string.registrants, detailEvent.registrants)

        val remainingQuota = detailEvent.quota - detailEvent.registrants
        binding.eventRemainingQuotaTextView.text = getString(R.string.remaining_quota, remainingQuota.toString())
        binding.eventCategoryTextView.text = getString(R.string.category, detailEvent.category)

        Glide.with(this)
            .load(detailEvent.mediaCover)
            .into(binding.eventImageView)

        binding.eventLinkButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(detailEvent.link)
            startActivity(intent)
        }
    }

    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }
}
