package com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels

import android.location.Location
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {

    private val _languageCode = MutableStateFlow("uz")
    val languageCode: StateFlow<String> get() = _languageCode

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location

    /**
     * Changes the application's language.
     * @param languageCode The new language code to set.
     */
    fun changeLanguage(languageCode: String) {
        _languageCode.value = languageCode
    }

    fun getCurrentLanguage(): String {
        return _languageCode.value
    }

    /**
     * Updates the current location.
     * @param location The new location to set.
     */
    fun updateLocation(location: Location) {
        _location.value = location
    }
}
