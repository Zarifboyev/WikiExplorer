package com.example.newsapp.domain
import com.example.newsapp.data.entity.ArticleTestEntity
import com.example.newsapp.data.entity.WikiEntity

interface WikiRepository {
    fun getAllWiki(): List<WikiEntity>
//    fun saveWiki(wikiEntity: WikiEntity)
}