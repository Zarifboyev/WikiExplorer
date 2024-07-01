package com.example.newsapp.presentation.screen
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.ScreenHomeBinding
import com.example.newsapp.presentation.adapters.PlacesAdapter
import com.example.newsapp.presentation.viewModels.HomeViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import com.google.android.material.progressindicator.LinearProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home) {

    private val binding by viewBinding(ScreenHomeBinding::bind)
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var placesViewModel: HomeViewModel
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private lateinit var progressIndicator: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesViewModel = ViewModelProvider(this)[HomeViewModelImpl::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressIndicator = view.findViewById(R.id.linearProgressIndicator2)

        setupRecyclerView()
        observeViewModel()
        getLocation()
    }

    private fun setupRecyclerView() {
        placesAdapter = PlacesAdapter(requireContext())
        binding.placesList.layoutManager = LinearLayoutManager(requireContext())
        binding.placesList.adapter = placesAdapter

        binding.placesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.btnSearch.hide()
                } else if (dy < 0) {
                    binding.btnSearch.show()
                }
            }
        })
        binding.btnSearch.setOnClickListener {
            getLocation()
        }
    }

    private fun observeViewModel() {
        placesViewModel.places.observe(viewLifecycleOwner) { placesList ->
            placesAdapter.submitList(placesList)
            Timber.tag("Places").d(placesList.toString())
            progressIndicator.visibility = View.GONE // Hide the progress indicator when data is received
        }
    }

    private fun getLocation() {
        progressIndicator.visibility = View.VISIBLE // Show the progress indicator

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                placesViewModel.fetchPlaces(location.latitude, location.longitude)
                locationManager?.removeUpdates(this) // Stop location updates to save battery
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        locationManager?.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener!!, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                // Permission denied
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                progressIndicator.visibility = View.GONE // Hide the progress indicator if permission is denied
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationManager?.removeUpdates(locationListener!!)
    }
}
