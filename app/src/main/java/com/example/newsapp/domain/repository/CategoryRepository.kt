package com.example.newsapp.domain.repository

import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.utils.toDomain
import com.example.newsapp.utils.toEntity

import android.util.Log
import com.example.newsapp.data.dao.WikiTaskDao
import com.example.newsapp.domain.service.CategoryMember
import com.example.newsapp.domain.service.WikipediaApiService
import com.example.newsapp.utils.toDomain
import com.example.newsapp.utils.toEntity
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val apiService: WikipediaApiService,
    private val categoryMemberDao: WikiTaskDao
) {
    suspend fun getCategoryMembers(category: String): List<CategoryMember> {
        return try {
            val response = apiService.getCategoryMembers(category = category)

            if (response.isSuccessful) {
                val categoryMembers = response.body()?.query?.categorymembers ?: emptyList()
                categoryMemberDao.insertAll(categoryMembers.map { it.toEntity() })
                categoryMembers
            } else {
                // Fetch from local database in case of an error
                categoryMemberDao.getCategoryMembersByNamespace(0).map { it.toDomain() }
            }
        } catch (e: Exception) {
            // Fetch from local database in case of an error
            categoryMemberDao.getCategoryMembersByNamespace(0).map { it.toDomain() }
        }
    }
}
