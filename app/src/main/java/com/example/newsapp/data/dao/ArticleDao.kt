package com.example.newsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.entity.ArticleTestEntity


@Dao
interface ArticleDao {
    @Query("SELECT * FROM articlesTable")
    fun getAllArticles(): List<ArticleTestEntity>

    @Query("SELECT * FROM articlesTable where article_wiki_text=:category_id")
    fun getArticlesByCategoryId(category_id: Int): List<ArticleTestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticlesToWiki(notesEntity: ArticleTestEntity)

    @Delete
    fun deleteArticle(notesEntity: ArticleTestEntity)


}