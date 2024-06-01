package com.example.newsapp.domain.repository

import com.example.newsapp.data.entity.ArticleTestEntity

interface ArticleRepository {
    fun getAllArticles(): List<ArticleTestEntity>
    fun insertArticles(categoriesEntity: ArticleTestEntity)
    fun deleteArticles(categoryId:Int)
    fun updateArticles(categoriesEntity: ArticleTestEntity)
}