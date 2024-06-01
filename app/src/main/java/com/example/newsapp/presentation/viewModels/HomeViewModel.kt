package uz.mlsoft.noteappnative.presentaion.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.data.entity.WikiEntity

interface HomeViewModel {
    val moveToInfoScreen: LiveData<Boolean>//state
    val fetchWikiNewsData: LiveData<List<WikiEntity>>

    fun loadData()//event
    fun moveToInfoScreen()
}