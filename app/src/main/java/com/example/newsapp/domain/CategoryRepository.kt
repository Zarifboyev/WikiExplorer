package com.example.newsapp.domain

import com.example.newsapp.data.entity.CategoryEntity

interface CategoryRepository {
    fun getAllCategories(): List<CategoryEntity>
    fun insertCategories(categoriesEntity: CategoryEntity)
    fun deleteCategories(categoryId:Int)
    fun updateCategories(categoriesEntity: CategoryEntity)
}