package com.example.newsapp.presentation.screen

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentMapDialogBinding
import com.example.newsapp.presentation.viewModels.GlobalViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class MapDialogFragment : DialogFragment(), OnMapReadyCallback, LocationListener {

    private lateinit var googleMap: GoogleMap
    private var selectedLocation: Location? = null
    private var _binding: FragmentMapDialogBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: GlobalViewModel by activityViewModels()
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapDialogBinding.inflate(inflater, container, false)
        setupToolbar()
        setupMapFragment()
        return binding.root
    }

    private fun setupToolbar() {
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.baseline_arrow_back_24)
            (requireActivity() as? AppCompatActivity)?.setSupportActionBar(this)

            setNavigationOnClickListener {
                Timber.d("Back button clicked")
                // Delayed dismissal
                view?.postDelayed({
                    dialog?.dismiss()
                }, 300) // Adjust delay as needed (300 milliseconds here)
            }
        }

        binding.btnGo.setOnClickListener {
            selectedLocation?.let {
                // Mana shu yeriga e'tibor berchi
               locationViewModel.updateLocation(it)
                // Delayed dismissal
                view?.postDelayed({
                    dialog?.dismiss()
                }, 300) // Adjust delay as needed (300 milliseconds here)
            } ?: run {
                Snackbar.make(binding.root, getString(R.string.select_location_on_map), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMapFragment() {
        val mapFragment = SupportMapFragment.newInstance().also {
            childFragmentManager.beginTransaction().replace(R.id.map_container, it).commit()
        }
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        Timber.d("Map is ready")
        googleMap = map

        // Enable all map controls and gestures
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomGesturesEnabled = true
        }


        // Bu yerga ham qara, tepasi

        // Get user’s location and move the camera
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
        } else {
            Timber.e("Location permission not granted")
            Snackbar.make(binding.root, getString(R.string.location_not_granted), Snackbar.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Add map click listener to select location
        googleMap.setOnMapClickListener { latLng ->
            selectedLocation = Location("").apply {
                latitude = latLng.latitude
                longitude = latLng.longitude
            }
            Timber.d("Map clicked, selected location: $latLng")
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))
        }
    }

    override fun onLocationChanged(location: Location) {
        val userLatLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
        locationManager.removeUpdates(this) // Stop receiving location updates
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.d("Creating dialog with full screen theme")
        return Dialog(requireContext(), R.style.MapDialogTheme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("MapDialogFragment onDestroyView")
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
