package com.example.newsapp.domain.repository
import com.example.newsapp.data.entity.WikiModel
import io.github.fastily.jwiki.core.Wiki

interface WikiRepository {
    suspend fun getAllWiki(): List<WikiModel>
    fun saveWiki(wikiEntity: WikiModel)
    suspend fun getWikiBuilder():Wiki.Builder

}