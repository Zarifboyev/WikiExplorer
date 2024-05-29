package com.example.newsapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.dao.CategoryDao
import com.example.newsapp.data.dao.NoteDao
import com.example.newsapp.data.entity.CategoryEntity
import com.example.newsapp.data.entity.NoteEntity

@Database(entities = [NoteEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class NotesDatabase :RoomDatabase() {
    abstract fun getNotesDao(): NoteDao
    abstract fun getCategoryDao(): CategoryDao
}