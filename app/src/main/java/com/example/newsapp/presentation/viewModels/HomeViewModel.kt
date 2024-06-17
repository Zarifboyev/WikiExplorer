package com.example.newsapp.presentation.viewModels

import androidx.lifecycle.LiveData
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.data.model.Place
import io.github.fastily.jwiki.core.Wiki
import io.github.fastily.jwiki.core.Wiki.Builder

interface HomeViewModel {
    val moveToInfoScreen: LiveData<Boolean>//state
    val places: LiveData<List<Place>>
    fun loadData(builder: Builder)//event
    fun moveToInfoScreen()
    fun fetchPlaces(lat: Double, lon: Double)

}