package com.example.newsapp.presentation.viewModels.impl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.repository.WikipediaRepository
import com.example.newsapp.domain.service.Section
import com.example.newsapp.presentation.viewModels.WikipediaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WikipediaViewModelImpl @Inject constructor(
    private val repository: WikipediaRepository
) : ViewModel(), WikipediaViewModel {

    private val _sections = MutableLiveData<List<Section>>()
    override val sections: LiveData<List<Section>> get() = _sections

    private val _redLinks = MutableLiveData<List<String>>()
    override val redLinks: LiveData<List<String>> get() = _redLinks

    override fun fetchSections(page: String) {
        viewModelScope.launch {
            val response = repository.getPageSections(page)
            if (response.isSuccessful) {
                _sections.value = response.body()?.parse?.sections
            }
        }
    }

    override fun fetchRedLinks(title: String) {
        viewModelScope.launch {
            val response = repository.getRedLinks(title)
            if (response.isSuccessful) {
                _redLinks.value = response.body()?.query?.pages?.values
                    ?.filter { it.missing != null }
                    ?.map { it.title } ?: emptyList()
            }
        }
    }
}
