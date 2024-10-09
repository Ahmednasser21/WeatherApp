package com.ahmed.weather.iti.ui.alarm

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ahmed.weather.iti.databinding.FragmentAlarmBinding
import com.ahmed.weather.iti.ui.notification.WeatherNotificationHelper
import java.util.Calendar

class AlarmFragment : Fragment() {

    companion object {
        private const val RINGTONE_PICKER_REQUEST_CODE = 1001
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    }

    private lateinit var selectedSoundUri: Uri
    private lateinit var fire: Button
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alarmViewModel = ViewModelProvider(this)[AlarmViewModel::class.java]
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fire = binding.button
//        val workRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(3, TimeUnit.HOURS).build()
//        WorkManager.getInstance(requireContext()).enqueue(workRequest)

        calendar = Calendar.getInstance()

        fire.setOnClickListener {
            checkNotificationPermission()
        }

    }

    private fun showDateTimePicker(context: Context) {
        val currentDateTime = Calendar.getInstance()

        // Create the DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)

                        val notificationHelper = WeatherNotificationHelper(context)
                        notificationHelper.scheduleNotification(
                            context,
                            selectedDateTime.timeInMillis,
                            selectedSoundUri
                        )
                        val formattedDateTime = android.text.format.DateFormat.format(
                            "yyyy-MM-dd HH:mm", selectedDateTime
                        )
                        Toast.makeText(
                            context,
                            "Alarm set for $formattedDateTime",
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    currentDateTime.get(Calendar.HOUR_OF_DAY),
                    currentDateTime.get(Calendar.MINUTE),
                    true
                ).show()
            },
            currentDateTime.get(Calendar.YEAR),
            currentDateTime.get(Calendar.MONTH),
            currentDateTime.get(Calendar.DAY_OF_MONTH)
        )

        // Set minimum date to current time to disable past dates
        datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis
        datePickerDialog.show()
    }


    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                openSoundPicker()
            }
        } else {
            openSoundPicker()
        }
    }

    private fun openSoundPicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        }
        startActivityForResult(intent, RINGTONE_PICKER_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RINGTONE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? =
                data?.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                selectedSoundUri = uri
                val ringtone = android.media.RingtoneManager.getRingtone(requireContext(), uri)
                Toast.makeText(
                    requireContext(),
                    "Selected sound: ${ringtone.getTitle(requireContext())}",
                    Toast.LENGTH_SHORT
                ).show()
                showDateTimePicker(requireContext())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openSoundPicker()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission is required to set alarms",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}