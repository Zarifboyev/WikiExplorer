package com.example.newsapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val _isLocationPermissionGranted = MutableStateFlow(isPermissionGranted())
    val isLocationPermissionGranted: StateFlow<Boolean> get() = _isLocationPermissionGranted

    private val _isLocationEnabled = MutableStateFlow(isLocationEnabled())
    val isLocationEnabled: StateFlow<Boolean> get() = _isLocationEnabled

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location

    init {
        updateLocationEnabled()
    }

    @SuppressLint("MissingPermission")
    suspend fun updateLocation(context: Context) {
        if (isPermissionGranted()) {
            try {
                val location: Location? = fusedLocationClient.lastLocation.await()
                _location.value = location
            } catch (e: Exception) {
                // Handle the exception or log it as needed
                _location.value = null
            }
        } else {
            _location.value = null
        }
    }

    private fun isPermissionGranted(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    fun updateLocationEnabled() {
        _isLocationEnabled.value = isLocationEnabled()
    }
}