package com.example.edicoding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.edicoding.databinding.FragmentSettingsBinding
import com.example.edicoding.util.ThemePreferences
import com.example.edicoding.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var themePreferences: ThemePreferences
    private lateinit var workManager: WorkManager
    private lateinit var dailyReminderSwitch: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        themePreferences = ThemePreferences(requireContext())
        workManager = WorkManager.getInstance(requireContext())

        binding.themeSwitch.isChecked = themePreferences.isDarkMode()

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                themePreferences.setDarkMode(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                themePreferences.setDarkMode(false)
            }
        }

        dailyReminderSwitch = binding.dailyReminderSwitch
        dailyReminderSwitch.isChecked = themePreferences.isDailyReminderActive()

        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            themePreferences.setDailyReminderActive(isChecked)
            if (isChecked) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        workManager.cancelUniqueWork("DailyReminder")
    }
}
