package com.ahmed.weather.iti.ui.favourite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.ahmed.weather.iti.R
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.database.LocalDataSource
import com.ahmed.weather.iti.databinding.FragmentFavouriteBinding
import com.ahmed.weather.iti.network.RemoteDataSource
import com.ahmed.weather.iti.ui.maps.LocationSharedVM
import com.ahmed.weather.iti.network.RetrofitObj
import com.ahmed.weather.iti.repository.Repository
import com.ahmed.weather.iti.ui.maps.LocationData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment(), OnDeleteClickListener,OnFavItemClickListener {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var favImg:ImageView
    private lateinit var emptyText:TextView
    private lateinit var favouriteRecycler: RecyclerView
    private lateinit var addFavourite: FloatingActionButton
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var favouriteViewModel: FavouriteViewModel
    private val sharedVM: LocationSharedVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = FavouriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(RetrofitObj.service),
                LocalDataSource(DataBase.getInstance(requireContext()))
            )
        )
        favouriteViewModel = ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favImg= binding.imgFav
        emptyText = binding.tvEmptyFav
        favouriteAdapter = FavouriteAdapter(this,this)
        favouriteRecycler = binding.recFavourite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favouriteAdapter
        }
        addFavourite = binding.abFavourite.apply {
            setOnClickListener {
                val action = FavouriteFragmentDirections.actionNavFavouriteToNavMaps("fav")
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
        addNewFavLocation()
        favouriteViewModel.getAllFav()
        setFavList()
    }

    private fun addNewFavLocation() {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedVM.favLocationData.collectLatest {
                if(it.cityName.isNotBlank()) {
                    val favouriteDTO = FavouriteDTO(it.cityName, it.longitude, it.latitude)
                    favouriteViewModel.addFav(favouriteDTO)
                }
            }
        }
    }

    private fun setFavList() {
        lifecycleScope.launch {
            favouriteViewModel.favList.collectLatest {
                withContext(Dispatchers.Main) {
                    showUI(it.isEmpty())
                    favouriteAdapter.submitList(it)
                }
            }
        }
    }

    override fun onClick(favouriteDto: FavouriteDTO) {
        showDeleteAlert(favouriteDto)
    }

    private fun showDeleteAlert(favouriteDto: FavouriteDTO) {

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_WeatherApp_Dialog).apply {
            setTitle("Are you sure")
            setMessage("Do you want to delete this location from favourites?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    favouriteViewModel.removeFav(favouriteDto)
                }
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()

    }
    private fun showUI(isListEmpty:Boolean){
        if(isListEmpty){
            favouriteRecycler.visibility =View.GONE
            favImg.visibility = View.VISIBLE
            emptyText.visibility =View.VISIBLE
        }else{
            favouriteRecycler.visibility =View.VISIBLE
            favImg.visibility = View.GONE
            emptyText.visibility =View.GONE
        }
    }

    override fun onItemClick(favouriteDTO: FavouriteDTO) {
        sharedVM.sendMainLocationData(LocationData(favouriteDTO.longitude,favouriteDTO.latitude,favouriteDTO.cityName))
        val action = FavouriteFragmentDirections.actionNavFavouriteToNavHome(true)
        Navigation.findNavController(requireView()).navigate(action)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.favourite)
    }
}