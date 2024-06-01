package uz.mlsoft.noteappnative.presentaion.viewModels.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val repository: WikiRepository
) : ViewModel(), HomeViewModel {

    private val _moveToInfoScreen = MutableLiveData<Boolean>()
    override val moveToInfoScreen: LiveData<Boolean> get() = _moveToInfoScreen

    private val _fetchWikiNewsData = MutableLiveData<List<WikiModel>>()
    override val fetchWikiNewsData: LiveData<List<WikiModel>> get() = _fetchWikiNewsData

    override fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Simulate data fetching with sample values
            val sampleData = listOf(
                WikiModel(id = 1, title = "Sample Title 1", description = "Sample Description 1", image = "https://example.com/image1.jpg"),
                WikiModel(id = 2, title = "Sample Title 2", description = "Sample Description 2", image = "https://example.com/image2.jpg"),
                WikiModel(id = 3, title = "Sample Title 3", description = "Sample Description 3", image = "https://example.com/image3.jpg"),
                WikiModel(id = 4, title = "Sample Title 4", description = "Sample Description 4", image = "https://example.com/image4.jpg"),
                WikiModel(id = 5, title = "Sample Title 5", description = "Sample Description 5", image = "https://example.com/image5.jpg")
            )

            _fetchWikiNewsData.postValue(sampleData)
        }
    }

    override fun moveToInfoScreen() {
        _moveToInfoScreen.value = true
    }
}
