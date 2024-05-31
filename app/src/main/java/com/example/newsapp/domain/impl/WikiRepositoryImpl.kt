package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.domain.WikiRepository
import javax.inject.Inject

class WikiRepositoryImpl @Inject constructor(private val wikiDao: WikiDao) : WikiRepository {
    override fun getAllWiki(): List<WikiEntity> = wikiDao.getAllArticles()
    override fun saveWiki(wikiEntity: WikiEntity) {
        wikiDao.saveWiki(wikiEntity)
    }

}