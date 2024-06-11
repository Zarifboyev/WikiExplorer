package com.example.newsapp.presentation.viewModels.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.presentation.viewModels.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fastily.jwiki.core.Wiki
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val repository: WikiRepository,
    override val wikiBuilder: Wiki.Builder
) : ViewModel(), HomeViewModel {

    private val _moveToInfoScreen = MutableLiveData<Boolean>()
    override val moveToInfoScreen: LiveData<Boolean> get() = _moveToInfoScreen

    private val _fetchWikiNewsData = MutableLiveData<List<WikiModel>>()
    override val fetchWikiNewsData: LiveData<List<WikiModel>> get() = _fetchWikiNewsData


    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            val data = repository.getAllWiki()
            _fetchWikiNewsData.postValue(data)
        }
    }

    override fun moveToInfoScreen() {
        _moveToInfoScreen.value = true
    }




}
