package com.example.newsapp.domain.repository
import com.example.newsapp.data.entity.ArticleTestEntity
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel

interface WikiRepository {
    fun getAllWiki(): List<WikiModel>
    fun saveWiki(wikiEntity: WikiEntity)


}