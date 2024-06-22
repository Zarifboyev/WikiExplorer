package com.example.newsapp.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.entity.TasksEntity
import com.example.newsapp.domain.repository.WikiTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WikiTaskViewModel @Inject constructor(private val repository: WikiTaskRepository) : ViewModel() {
    val allGoals: LiveData<List<TasksEntity>> = repository.allGoals

    fun insert(goal: TasksEntity) = viewModelScope.launch {
        repository.insert(goal)
    }

    fun delete(goal: TasksEntity) = viewModelScope.launch {
        repository.delete(goal)
    }
    fun update(goal: TasksEntity) = viewModelScope.launch {
        repository.update(goal)
    }
}