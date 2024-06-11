package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.domain.service.WikiService
import io.github.fastily.jwiki.core.Wiki
import javax.inject.Inject

class WikiRepositoryImpl @Inject constructor(private val wikiService: WikiService,
                                             private val wikiDao: WikiDao,
) : WikiRepository {


    override suspend fun getAllWiki(): List<WikiModel> {
            // Check cache
            val cachedData = wikiDao.getAllArticles()
            if (cachedData.isNotEmpty()) {
                return cachedData
            }

            // Fetch from network

            val newData = wikiService.fetchArticles(getWikiBuilder())

            // Cache data
            wikiDao.insertWikiData(newData)

            return newData
    }


    override fun saveWiki(wikiEntity: WikiModel) {
        //TODO: Save to database
    }


    override suspend fun getWikiBuilder(): Wiki.Builder {
        return Wiki.Builder()
    }


}