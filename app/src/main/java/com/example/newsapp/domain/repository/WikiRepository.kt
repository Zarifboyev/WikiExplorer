package com.example.newsapp.domain.repository
import com.example.newsapp.data.entity.WikiModel

interface WikiRepository {
    fun saveWiki(wikiEntity: WikiModel)
    suspend fun fetchArticles():List<WikiModel>
}