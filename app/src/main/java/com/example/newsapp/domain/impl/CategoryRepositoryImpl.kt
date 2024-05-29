package com.example.newsapp.domain.impl

import com.example.newsapp.data.dao.CategoryDao
import com.example.newsapp.data.entity.CategoryEntity
import com.example.newsapp.domain.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(val categoriesDao: CategoryDao) :
    CategoryRepository {
    override fun getAllCategories(): List<CategoryEntity> = categoriesDao.getAllCategories()

    override fun insertCategories(categoriesEntity: CategoryEntity) {
        categoriesDao.insertCategories(categoriesEntity)
    }

    override fun deleteCategories(categoryId: Int) {
        categoriesDao.deleteNotesByCategoryId(categoryId)
    }


    override fun updateCategories(categoriesEntity: CategoryEntity) {
        categoriesDao.insertCategories(categoriesEntity)
    }
}