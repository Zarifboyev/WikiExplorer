package com.example.newsapp.presentation.viewModels.impl

import androidx.lifecycle.ViewModel
import com.example.newsapp.domain.repository.WikiRepository
import com.example.newsapp.presentation.viewModels.NotesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class NotesViewModelImpl @Inject constructor(
    private val repository: WikiRepository
) : NotesViewModel, ViewModel() {

//    override val notesListLivedata = MutableLiveData<List<ArticleTestEntity>>()
//
//    override val showPlaceHolderLiveData = MutableLiveData<Boolean>()
//
//    override val moveToAddFragmentLiveData = MutableLiveData<Int>()
//    override val moveToHomeLivedata = MutableLiveData<Unit>()
//
//    override fun clickAdd(categoryId: Int) {
//        moveToAddFragmentLiveData.value = categoryId
//    }
//
//    override fun deleteNotes(notesEntity: ArticleTestEntity) {
//        repository.deleteWikis(notesEntity)
//    }
//
//    override fun editNotes(notesEntity: ArticleTestEntity) {
//        repository.updateWikis(notesEntity)
//    }
//
//    override fun setTime(time: Long) {
//
//    }
//
//    override fun updateList(categoryId: Int) {
//        val notesList = repository.getAllWikiById(categoryId)
//        notesListLivedata.value = notesList
//        myTimber("viewModel size${notesList.size}")
//        showPlaceHolderLiveData.value = notesList.isEmpty()
//    }
//
//    override fun clickBack() {
//        moveToHomeLivedata.value = Unit
//    }
}