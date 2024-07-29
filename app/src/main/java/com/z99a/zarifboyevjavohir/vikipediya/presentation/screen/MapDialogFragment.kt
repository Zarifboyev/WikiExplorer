package com.z99a.zarifboyevjavohir.vikipediya.presentation.screen

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.FragmentMapDialogBinding
import com.z99a.zarifboyevjavohir.vikipediya.playStoreUrl
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ForkJoinPool.ManagedBlocker

@AndroidEntryPoint
class MapDialogFragment : DialogFragment(), OnMapReadyCallback {

    private var _binding: FragmentMapDialogBinding? = null
    private val binding get() = _binding!!
    private val globalViewModel: GlobalViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment

    private var circle: Circle? = null

    private val defaultLatLng = LatLng(41.311081, 69.240562)
    private val defaultZoom = 12f
    private val closeZoom = 12f

    private var isToastShown = false

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            moveToCurrentLocation()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        setupMapFragment()
        setupFab()
        setupAutoCompleteFragment()
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.map_container, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun setupFab() {
        binding.fabMyLocation.setOnClickListener {
            checkLocationPermissionAndProceed()
        }
    }

    private fun setupAutoCompleteFragment() {
        autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autoCompleteFragment.setHint("Qidiruv")
        autoCompleteFragment.setActivityMode(AutocompleteActivityMode.OVERLAY)
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { latLng ->
                    zoomOnMap(latLng)
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
//                showSnackbar(R.string.error_occured)
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        configureMapUI()
        setDefaultLocation()
        loadSavedLocation() // Ensure this is called after googleMap is initialized
    }

    private fun configureMapUI() {
        googleMap.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = false
                isRotateGesturesEnabled = true
                isScrollGesturesEnabled = true
                isTiltGesturesEnabled = true
                isZoomGesturesEnabled = true
                isIndoorLevelPickerEnabled = true
            }
            mapType = GoogleMap.MAP_TYPE_NORMAL
            isBuildingsEnabled = true
            setOnMapClickListener { latLng -> onMapClick(latLng) }
            setOnMarkerClickListener { _ -> onMarkerClick() }
        }
    }

    private fun setDefaultLocation() {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom)
        googleMap.animateCamera(cameraUpdate)
        addMarkerAndCircle(defaultLatLng, getString(R.string.default_location))
    }

    private fun checkLocationPermissionAndProceed() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                moveToCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showLocationPermissionRationale()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showLocationPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Grant Location Permission")
            .setMessage("Location permission rationale")
            .setPositiveButton(R.string.ok) { _, _ ->
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun moveToCurrentLocation() {
        if (::googleMap.isInitialized && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.clear()
                    zoomOnMap(currentLatLng)
                    addMarkerAndCircle(currentLatLng, getString(R.string.current_location))
                    saveLocation(it)
                    globalViewModel.updateLocation(it)
                } ?: showSnackbar(R.string.location_not_granted)
            }.addOnFailureListener {
                showSnackbar(R.string.location_retrieval_failed)
            }
        } else {
            showSnackbar(R.string.location_not_granted)
        }
    }

    private fun zoomOnMap(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(closeZoom)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null)
    }

    private fun addMarkerAndCircle(latLng: LatLng, title: String) {
        googleMap.addMarker(MarkerOptions().position(latLng).title(title))
        drawCircleAroundMarker(latLng)
    }

    private fun drawCircleAroundMarker(location: LatLng) {
        circle?.remove()
        circle = googleMap.addCircle(
            CircleOptions()
                .center(location)
                .radius(2500.0)
                .strokeColor(0x550000FF)
                .fillColor(0x220000FF)
                .strokeWidth(2f)
        )
    }

    private fun onMapClick(latLng: LatLng) {
        val location = Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
        googleMap.clear()
        addMarkerAndCircle(latLng, getString(R.string.selected_location))
        saveLocation(location)
        globalViewModel.updateLocation(location)
        if (!isToastShown) {
            showSnackbar(R.string.tap_marker_to_select)
            isToastShown = true
        }
    }

    private fun onMarkerClick(): Boolean {
        dismissWithDelay()
        return true
    }

    private fun showSnackbar(messageResId: Int) {
        Snackbar.make(requireView(), getString(messageResId), Snackbar.LENGTH_SHORT).show()
    }

    private fun dismissWithDelay() {
        view?.postDelayed({
            dialog?.dismiss()
        }, 300)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.MapDialogTheme)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::googleMap.isInitialized) {
            googleMap.setOnMapClickListener(null)
            googleMap.setOnMarkerClickListener(null)
        }
    }

    private fun saveLocation(location: Location) {
        val sharedPreferences = requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat("latitude", location.latitude.toFloat())
            putFloat("longitude", location.longitude.toFloat())
            apply()
        }
    }

    private fun loadSavedLocation() {
        val sharedPreferences = requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat("latitude", Float.MIN_VALUE)
        val longitude = sharedPreferences.getFloat("longitude", Float.MIN_VALUE)
        if (latitude != Float.MIN_VALUE && longitude != Float.MIN_VALUE) {
            val savedLocation = Location("").apply {
                this.latitude = latitude.toDouble()
                this.longitude = longitude.toDouble()
            }
            globalViewModel.updateLocation(savedLocation)
            val savedLatLng = LatLng(savedLocation.latitude, savedLocation.longitude)
            googleMap.clear() // Clear previous markers and circles
            zoomOnMap(savedLatLng)
            addMarkerAndCircle(savedLatLng, getString(R.string.saved_location))
        }
    }
}
