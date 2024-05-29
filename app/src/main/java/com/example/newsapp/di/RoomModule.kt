package com.example.newsapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.dao.CategoryDao
import com.example.newsapp.data.dao.NoteDao
import com.example.newsapp.data.database.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @[Provides Singleton]
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(context, NotesDatabase::class.java, "note.db")
            .allowMainThreadQueries().build()
    }


    @[Singleton Provides]
    fun getCategoriesDao(database: NotesDatabase): CategoryDao = database.getCategoryDao()

    @[Singleton Provides]
    fun getNoteDao(database: NotesDatabase): NoteDao = database.getNotesDao()
}