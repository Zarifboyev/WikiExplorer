package com.example.newsapp.presentation.viewModels

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.utils.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    private val _languageCode = MutableStateFlow("uz")
    val languageCode: StateFlow<String> get() = _languageCode

    private val _isLocationAvailable = MutableStateFlow(false)
    val isLocationAvailable: StateFlow<Boolean> get() = _isLocationAvailable

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location

    init {
        viewModelScope.launch {
            observeLocationSettings()
        }
    }

    fun changeLanguage(languageCode: String) {
        _languageCode.value = languageCode
    }

    private fun observeLocationSettings() {

        viewModelScope.launch {
            locationManager.location.collect { location ->
                _location.value = location
                _isLocationAvailable.value = location != null
            }
        }
    }

    // Location Manager o'zi ishlamayapti, hatto mapda ham tugma bosilganda lokatsiya olib kelmagan
    // bo'lishi mumkin
    fun fetchLocation() {
        viewModelScope.launch {
            locationManager.updateLocation()
        }
    }

     fun updateLocation(it: Location) {
            _location.value = it
    }
}