package uz.mlsoft.noteappnative.presentaion.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel
import io.github.fastily.jwiki.core.Wiki

interface HomeViewModel {
    val moveToInfoScreen: LiveData<Boolean>//state
    val fetchWikiNewsData: LiveData<List<WikiModel>>
    val wikiBuilder: Wiki.Builder
    fun loadData()//event
    fun moveToInfoScreen()

}