package com.example.newsapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.newsapp.data.entity.TasksEntity
import com.example.newsapp.data.entity.WikiModel

@Dao
interface WikiTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryMembers: List<WikiModel>)

    @Query("SELECT * FROM wiki_articles WHERE ns = :ns")
    suspend fun getCategoryMembersByNamespace(ns: Int): List<WikiModel>
}
