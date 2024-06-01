package uz.mlsoft.noteappnative.presentaion.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel

interface HomeViewModel {
    val moveToInfoScreen: LiveData<Boolean>//state
    val fetchWikiNewsData: LiveData<List<WikiModel>>

    fun loadData()//event
    fun moveToInfoScreen()

}