package com.example.newsapp.presentation.viewModels.impl

import androidx.lifecycle.ViewModel
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.presentation.viewModels.YouTubeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel

class YouTubeViewModelImpl @Inject constructor(
    private val repository: WikiRepository
) : YouTubeViewModel, ViewModel() {

}



