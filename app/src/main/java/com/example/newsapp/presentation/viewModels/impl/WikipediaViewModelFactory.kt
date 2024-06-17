package com.example.newsapp.presentation.viewModels.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.domain.repository.WikipediaRepository
import javax.inject.Inject

class WikipediaViewModelFactory @Inject constructor(
    private val repository: WikipediaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WikipediaViewModelImpl::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WikipediaViewModelImpl(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
