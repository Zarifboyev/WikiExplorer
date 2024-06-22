package com.example.newsapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "goals")
data class TasksEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    var isDone: Boolean = false,
    val color: Int,
    val timestamp: Long // This field represents the timestamp of when the task was created or last modified
) : Serializable
