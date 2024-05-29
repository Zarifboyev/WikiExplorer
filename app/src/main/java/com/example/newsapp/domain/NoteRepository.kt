package com.example.newsapp.domain
import com.example.newsapp.data.entity.NoteEntity

interface NoteRepository {
    fun getAllNotes(): List<NoteEntity>
    fun getAllNotesById(category_id: Int): List<NoteEntity>
    fun insertNotes(notesEntity: NoteEntity)
    fun updateNotes(notesEntity: NoteEntity)
    fun deleteNotes(notesEntity: NoteEntity)
}