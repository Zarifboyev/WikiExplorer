package com.example.newsapp.presentation.viewModels

import androidx.lifecycle.LiveData
import com.example.newsapp.domain.service.Section
import com.example.newsapp.domain.service.WikipediaStats
import dagger.hilt.android.lifecycle.HiltViewModel

interface WikipediaViewModel {
    val sections: LiveData<List<Section>>

    val redLinks: LiveData<List<String>>

    fun fetchSections(page: String)

    fun fetchRedLinks(title: String)}
