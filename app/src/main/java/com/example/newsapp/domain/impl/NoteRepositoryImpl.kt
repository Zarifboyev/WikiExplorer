package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.NoteDao
import com.example.newsapp.data.entity.NoteEntity
import com.example.newsapp.domain.NoteRepository
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val noteDao: NoteDao) : NoteRepository {
    override fun getAllNotes(): List<NoteEntity> = noteDao.getAllNotes()
    override fun getAllNotesById(category_id: Int): List<NoteEntity> =
        noteDao.getNotesByCategoryId(category_id = category_id)

    override fun insertNotes(notesEntity: NoteEntity) {
        noteDao.insertNotesToCategories(notesEntity = notesEntity)
    }

    override fun updateNotes(notesEntity: NoteEntity) {
        noteDao.insertNotesToCategories(notesEntity = notesEntity)
    }

    override fun deleteNotes(notesEntity: NoteEntity) {
        noteDao.deleteNote(notesEntity)
    }
}