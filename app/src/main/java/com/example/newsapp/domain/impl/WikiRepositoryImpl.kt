package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.WikiDao
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.domain.WikiRepository
import javax.inject.Inject

class WikiRepositoryImpl @Inject constructor(private val wikiDao: WikiDao) : WikiRepository {
    override fun getAllWiki(): List<WikiEntity> {
        //internet dan data olinadi

//        savWiki()
        return wikiDao.getAllArticles()
    }
//    override fun saveWiki(wikiEntity: WikiEntity) {
//        wikiDao.saveWiki(wikiEntity)
//    }


    private fun savWiki(wikiEntity: WikiEntity) {
        wikiDao.saveWiki(wikiEntity)
    }

}