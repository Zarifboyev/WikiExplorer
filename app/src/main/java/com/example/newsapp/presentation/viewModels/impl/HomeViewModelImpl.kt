package uz.mlsoft.noteappnative.presentaion.viewModels.impl

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fastily.jwiki.core.Wiki
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val appContext: Context,
    private val repository: WikiRepository,
    override val wikiBuilder: Wiki.Builder
) : ViewModel(), HomeViewModel {

    private val _moveToInfoScreen = MutableLiveData<Boolean>()
    override val moveToInfoScreen: LiveData<Boolean> get() = _moveToInfoScreen

    private val _fetchWikiNewsData = MutableLiveData<List<WikiModel>>()
    override val fetchWikiNewsData: LiveData<List<WikiModel>> get() = _fetchWikiNewsData

    override fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Simulate data fetching with sample values
            val articles = repository.fetchArticles(appContext, wikiBuilder)
            _fetchWikiNewsData.postValue(articles)
        }
    }

    override fun moveToInfoScreen() {
        _moveToInfoScreen.value = true
    }
}
