package com.example.newsapp.presentation.viewModels

import com.example.newsapp.data.model.Page
import com.example.newsapp.data.model.Place
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModel {
    val places: StateFlow<List<Place>>
    val isLoading: StateFlow<Boolean>

    val error:StateFlow<String?>



    fun fetchPlaces(lat: Double, lon: Double, langCode: String)

}