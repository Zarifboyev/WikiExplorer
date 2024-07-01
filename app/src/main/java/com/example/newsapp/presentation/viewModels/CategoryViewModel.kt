package com.example.newsapp.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.repository.CategoryRepository
import com.example.newsapp.domain.service.CategoryMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _articles = MutableLiveData<List<CategoryMember>>()
    val articles: LiveData<List<CategoryMember>> get() = _articles

    fun fetchCategoryMembers(category: String) {
        viewModelScope.launch {
            try {
                val categoryMembers = repository.getCategoryMembers(category)
                _articles.value = categoryMembers
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
