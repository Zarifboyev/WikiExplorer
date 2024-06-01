package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.ArticleDao
import com.example.newsapp.data.entity.ArticleTestEntity
import com.example.newsapp.domain.repository.ArticleRepository
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(val articlesDao: ArticleDao) :
    ArticleRepository {
    override fun getAllArticles(): List<ArticleTestEntity> = articlesDao.getAllArticles()

    override fun insertArticles(categoriesEntity: ArticleTestEntity) {
        articlesDao.insertArticlesToWiki(categoriesEntity)
    }

    override fun deleteArticles(categoryId: Int) {
        TODO("Not yet implemented")
    }


    override fun updateArticles(categoriesEntity: ArticleTestEntity) {
        articlesDao.insertArticlesToWiki(categoriesEntity)
    }
}