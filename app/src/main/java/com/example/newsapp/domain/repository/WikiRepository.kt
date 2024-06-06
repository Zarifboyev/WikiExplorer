package com.example.newsapp.domain.repository
import android.content.Context
import com.example.newsapp.data.entity.ArticleTestEntity
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel
import io.github.fastily.jwiki.core.Wiki

interface WikiRepository {
    fun getAllWiki(): List<WikiModel>
    fun saveWiki(wikiEntity: WikiEntity)
    suspend fun fetchArticles(context: Context, builder:Wiki.Builder):List<WikiModel>
    suspend fun getWikiBuilder():Wiki.Builder


}