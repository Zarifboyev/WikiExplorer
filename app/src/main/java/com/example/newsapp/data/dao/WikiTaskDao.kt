package com.example.newsapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.newsapp.data.entity.TasksEntity

@Dao
interface WikiTaskDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): LiveData<List<TasksEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: TasksEntity)

    @Delete
    suspend fun delete(goal: TasksEntity)

    @Update
    suspend fun update(goal: TasksEntity)
}
