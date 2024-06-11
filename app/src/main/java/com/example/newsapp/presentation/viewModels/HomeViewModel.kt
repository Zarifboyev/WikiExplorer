package com.example.newsapp.presentation.viewModels

import androidx.lifecycle.LiveData
import com.example.newsapp.data.entity.WikiModel
import io.github.fastily.jwiki.core.Wiki
import io.github.fastily.jwiki.core.Wiki.Builder

interface HomeViewModel {
    val moveToInfoScreen: LiveData<Boolean>//state
    val fetchWikiNewsData: LiveData<List<WikiModel>>
    val wikiBuilder: Wiki.Builder

    fun loadData()//event
    fun moveToInfoScreen()

}