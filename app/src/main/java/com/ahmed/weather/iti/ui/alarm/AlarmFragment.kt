package com.ahmed.weather.iti.ui.alarm

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.databinding.FragmentAlarmBinding
import com.ahmed.weather.iti.network.RetrofitObj
import com.ahmed.weather.iti.repository.Repository
import com.ahmed.weather.iti.ui.home.DataState
import com.ahmed.weather.iti.ui.notification.WeatherNotificationHelper
import com.ahmed.weather.iti.ui.notification.WeatherNotificationReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmFragment : Fragment(), OnDeleteAlarmListener {

    companion object {
        private const val RINGTONE_PICKER_REQUEST_CODE = 1001
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    }

    private lateinit var selectedSoundUri: Uri
    private lateinit var addNewAlarm: FloatingActionButton
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var calendar: Calendar
    private lateinit var alarmRecycler: RecyclerView
    private lateinit var alarmViewModel: AlarmViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = AlarmViewModelFactory(
            Repository.getInstance(
                RetrofitObj,
                DataBase.getInstance(requireContext())
            )
        )
        alarmViewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNewAlarm = binding.fabAddAlarm
        alarmRecycler = binding.recAlarm

//        val workRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(3, TimeUnit.HOURS).build()
//        WorkManager.getInstance(requireContext()).enqueue(workRequest)

        val alarmAdapter = AlarmAdapter(this)
        alarmRecycler.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            alarmViewModel.alarmList.collectLatest { result ->
                when (result) {
                    is DataState.OnSuccess<*> -> {
                        val alarmList = result.data as List<AlarmDTO>
                        alarmAdapter.submitList(alarmList)
                    }

                    is DataState.OnFailed -> {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load alarms",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is DataState.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        alarmViewModel.getAlarmList()

        addNewAlarm.setOnClickListener {
            checkNotificationPermission()
        }

        calendar = Calendar.getInstance()
    }

    private fun showDateTimePicker(context: Context) {
        val currentDateTime = Calendar.getInstance()

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
                            "yyyy-MM-dd-HH:mm", selectedDateTime
                        )
                        Toast.makeText(
                            context,
                            "Alarm set for $formattedDateTime",
                            Toast.LENGTH_SHORT
                        ).show()
                        lifecycleScope.launch {
                            alarmViewModel.addAlarm(
                                AlarmDTO(
                                    formattedDateTime.toString(),
                                    selectedDateTime.timeInMillis
                                )
                            )
                        }

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

        datePickerDialog.datePicker.minDate = currentDateTime.timeInMillis
        datePickerDialog.show()
    }


    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
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
                data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                selectedSoundUri = uri
                val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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


    override fun onClick(alarmDTO: AlarmDTO) {
        showDeleteAlert(alarmDTO)
    }

    private fun showDeleteAlert(alarmDTO: AlarmDTO) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_WeatherApp_Dialog).apply {
            setTitle("Are you sure?")
            setMessage("Do you want to delete this alarm?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    alarmViewModel.deleteAlarm(alarmDTO)
                    val notificationHelper = WeatherNotificationHelper(requireContext())
                    notificationHelper.cancelScheduledAlarm(2002)
                    cancelAlarm()
                }
            }
            setNegativeButton("No", null)
        }
        dialog.show()
    }

    private fun cancelAlarm() {
        val intent = Intent(requireContext(), WeatherNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            2002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {alarmViewModel.deleteExpiredAlarms()  }
    }

}