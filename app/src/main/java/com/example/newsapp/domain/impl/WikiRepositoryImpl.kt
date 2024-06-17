package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import javax.inject.Inject

class WikiRepositoryImpl @Inject constructor(
                                             private val wikiDao: WikiDao,
) : WikiRepository {


    override fun saveWiki(wikiEntity: WikiModel) {
        //TODO: Save to database
    }

    override suspend fun fetchArticles(): List<WikiModel> {
        // Check cache
        val cachedData = wikiDao.getAllArticles()
        if (cachedData.isNotEmpty()) {
            return cachedData
        }

        // Fetch from network


        val pages = fetchArticles()

        // Cache data
        wikiDao.insertWikiData(pages)

        return pages
    }




}