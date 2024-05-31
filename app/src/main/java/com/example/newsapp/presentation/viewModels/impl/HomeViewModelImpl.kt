package uz.mlsoft.noteappnative.presentaion.viewModels.impl

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.domain.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val repository: WikiRepository
) : HomeViewModel, ViewModel() {


    override val moveToInfoScreen = MutableLiveData<WikiEntity>()
    override val fetchWikiNewsData = MutableLiveData<List<WikiEntity>>()

    override fun loadData() {
        fetchWikiNewsData.value = repository.getAllWiki()
    }

}