package com.ahmed.weather.iti.ui.maps

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.ahmed.weather.iti.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MapsFragment : Fragment() {
    companion object{
        private const val TAG = "MapsFragment"
    }
    private val sharedVM : LocationSharedVM by activityViewModels()
    private val actionInitial = MapsFragmentDirections.actionNavMapsToNavHome()
    private val actionFav = MapsFragmentDirections.actionNavMapsToNavFavourite()
   private lateinit var navFragmentName:String

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.setOnMapClickListener {
            googleMap.addMarker(MarkerOptions().position(it).title("User Position"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            val cityName = addresses?.get(0)?.adminArea.toString()
            lifecycleScope.launch {
                delay(300)
                showAlertDialog(it.longitude,it.latitude,cityName,googleMap)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navFragmentName = MapsFragmentArgs.fromBundle(arguments?:Bundle()).fragmentName
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun showAlertDialog(longitude:Double,latitude:Double,cityName:String,googleMap: GoogleMap) {
        val dialog = AlertDialog.Builder(requireContext(),R.style.Theme_WeatherApp_Dialog).apply {
            setTitle("Are you sure")
            setMessage("Are you sure? this is your desired location")
            setPositiveButton("Yes") { _, _ ->
                Log.i(TAG, "showAlertDialog: $longitude")
                Log.i(TAG, "showAlertDialog: $latitude")
                Log.i(TAG, "showAlertDialog: $cityName")
                if (navFragmentName == "initial") {
                    sharedVM.sendMainLocationData(LocationData(latitude, longitude, cityName))
                    Navigation.findNavController(requireView()).navigate(actionInitial)
                }else if(navFragmentName == "fav"){
                    sharedVM.sendFavLocationData(LocationData(latitude, longitude, cityName))
                    Navigation.findNavController(requireView()).navigate(actionFav)
                }
            }
            setNegativeButton("No"){dialog,_->
                googleMap.clear()
                dialog.dismiss()
            }
        }.create().show()
    }
}