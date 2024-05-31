package uz.mlsoft.noteappnative.presentaion.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.data.entity.WikiEntity

interface HomeViewModel {
    val moveToInfoScreen: MutableLiveData<WikiEntity>
    val fetchWikiNewsData: MutableLiveData<List<WikiEntity>>

    fun loadData()


}