package com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels

import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModel {
    val places: StateFlow<List<Place>>
    val isLoading: StateFlow<Boolean>

    val isExisted:StateFlow<Boolean?>

    fun fetchPlaces(lat: Double, lon: Double, langCode: String)
}