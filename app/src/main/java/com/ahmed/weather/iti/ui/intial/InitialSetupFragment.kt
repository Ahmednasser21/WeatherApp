package com.ahmed.weather.iti.ui.intial

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.databinding.FragmentInitialSetupBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.IOException
import java.util.Locale

class InitialSetupFragment : DialogFragment() {
    private lateinit var binding: FragmentInitialSetupBinding
    private lateinit var radioGroup: RadioGroup
    private lateinit var gpsRadioButton: RadioButton
    private lateinit var mapRadioButton: RadioButton
    private lateinit var notificationSwitch: SwitchMaterial
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var address: String
    private var longitudeVal: Double = 0.0
    private var latitudeVal: Double = 0.0
    private lateinit var startForResult: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var locationManager: LocationManager
    private lateinit var ok:Button


    companion object {
        private const val TAG = "InitialSetupFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitialSetupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioGroup = binding.radioGroup
        mapRadioButton = binding.radioMap
        gpsRadioButton = binding.radioGps
        ok =binding.btnOk
        notificationSwitch = binding.swchNotification

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    RESULT_OK -> startLocationUpdates()
                    RESULT_CANCELED -> Toast.makeText(
                        requireContext(),
                        "You will not be able to use our services",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        radioGroup.setOnCheckedChangeListener { group, checkId ->

            when (checkId) {
                R.id.radio_gps -> {
                    if (checkPermissions()){
                        if(isLocationEnabled()){
                            startLocationUpdates()
                        }else{
                            enableLocationServices()
                        }
                    }else{
                        requestPermissions()
                    }
                }

                R.id.radio_map -> {

                }
            }

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {
                    longitudeVal = location.longitude
                    latitudeVal = location.latitude
                    Log.i(TAG, "onLocationResult:${location.longitude }")
                    Log.i(TAG, "onLocationResult:${location.latitude }")
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addressLine = "${addresses[0].adminArea}}"
                            address = addressLine
                            Log.i(TAG, "onLocationResult: $addressLine")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        locationRequest = LocationRequest.Builder(5000)
            .setIntervalMillis(10000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (isLocationEnabled()) {
                    startLocationUpdates()
                } else {
                    enableLocationServices()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "refused location permission, you will not be able to use our services\n unless you gave us a location",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun requestPermissions() {
        requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
    }


    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun enableLocationServices() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { _ ->
            startLocationUpdates()
        }

        task.addOnFailureListener { exception ->

            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    startForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }

    }
}


