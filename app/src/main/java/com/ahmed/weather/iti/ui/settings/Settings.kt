package com.ahmed.weather.iti.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.databinding.FragmentSettingsBinding
import com.ahmed.weather.iti.ui.intial.InitialSetupFragment
import java.util.Locale

class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var rgLocation: RadioGroup
    private lateinit var rgLanguage: RadioGroup
    private lateinit var rgTemperature: RadioGroup
    private lateinit var rgNotifications: RadioGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupRadioGroups()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedSettings()

        rgLocation.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.rb_maps -> {
                    saveBooleanToPreferences(getString(R.string.location),"maps",true)
                    val action = SettingsDirections.actionNavSettingsToNavMaps("initial")
                    Navigation.findNavController(requireView()).navigate(action)
                }

                R.id.rb_gps -> {
                    saveBooleanToPreferences(getString(R.string.location),"maps",false)

                }
            }
        }

        rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_arabic -> {
                    saveBooleanToPreferences(getString(R.string.language),"arabic",false)
                    setLocale("ar")
                    restartActivity()
                }

                R.id.rb_english -> {
                    saveBooleanToPreferences(getString(R.string.language),"arabic",true)
                    setLocale("en")
                    restartActivity()
                }
            }

        }

        rgTemperature.setOnCheckedChangeListener { _, checkedId ->
            val sharedPref = getSharedPreferences(getString(R.string.temperature))
            val editor = sharedPref.edit()
            when (checkedId) {
                R.id.rb_celsius -> {
                    editor.putString(getString(R.string.temperature),"celsius")
                }

                R.id.rb_Kalvin -> {
                    editor.putString(getString(R.string.temperature),"kalvin")
                }

                R.id.rb_fahrenheit -> {
                    editor.putString(getString(R.string.temperature),"fahrenheit")
                }
            }
            editor.apply()
        }

        rgNotifications.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_enable -> {
                    saveBooleanToPreferences(getString(R.string.notifications),"enabled",true)
                }

                R.id.rb_disable -> {
                    saveBooleanToPreferences(getString(R.string.notifications),"enabled",true)
                }
            }
        }
    }

    private fun loadSavedSettings() {
        val locationPref = getSharedPreferences(getString(R.string.location))
        val isMaps = locationPref.getBoolean("maps", false)
        rgLocation.check(if (isMaps) R.id.rb_maps else R.id.rb_gps)

        val languagePref = getSharedPreferences(getString(R.string.language))
        val isArabic = languagePref.getBoolean("arabic", false)
        rgLanguage.check(if (isArabic) R.id.rb_arabic else R.id.rb_english)


        val temperaturePref = getSharedPreferences(getString(R.string.temperature))
        val temperatureUnit = temperaturePref.getString(getString(R.string.temperature), "kalvin")
        when (temperatureUnit) {
            "celsius" -> rgTemperature.check(R.id.rb_celsius)
            "kalvin" -> rgTemperature.check(R.id.rb_Kalvin)
            "fahrenheit" -> rgTemperature.check(R.id.rb_fahrenheit)
        }
        val notificationsPref = getSharedPreferences(getString(R.string.notifications))
        val isEnabled = notificationsPref.getBoolean("enabled", true)
        rgNotifications.check(if (isEnabled) R.id.rb_enable else R.id.rb_disable)
    }

    private fun setupRadioGroups() {

        rgLocation = binding.rgLocation
        rgLanguage = binding.rgLanguage
        rgTemperature = binding.rgDegree
        rgNotifications = binding.radioGroup2

    }

    private fun getSharedPreferences(name: String): SharedPreferences {
        return requireContext().getSharedPreferences(name, MODE_PRIVATE)
    }

    private fun saveBooleanToPreferences(name: String, key: String, value: Boolean) {
        val sharedPreference = getSharedPreferences(name)
        with(sharedPreference.edit()) {
            putBoolean(key, value)
            apply()
        }

    }


    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun restartActivity() {
        requireActivity().recreate()
    }
}