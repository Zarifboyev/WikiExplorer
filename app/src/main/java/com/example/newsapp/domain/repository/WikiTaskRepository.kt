package com.example.newsapp.domain.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.dao.WikiTaskDao
import com.example.newsapp.data.entity.TasksEntity
import javax.inject.Inject

// WikiTaskRepository.kt
class WikiTaskRepository @Inject constructor(private val wikiTaskDao: WikiTaskDao) {
    val allGoals: LiveData<List<TasksEntity>> = wikiTaskDao.getAllGoals()

    suspend fun insert(goal: TasksEntity) {
        wikiTaskDao.insert(goal)
    }

    suspend fun delete(goal: TasksEntity) {
        wikiTaskDao.delete(goal)
    }
    suspend fun update(goal: TasksEntity) {
        wikiTaskDao.update(goal)
    }
}


