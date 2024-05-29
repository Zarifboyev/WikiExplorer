package com.example.newsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    @Query("SELECT * FROM categoriesTable")
    fun getAllCategories():List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(entity: CategoryEntity)


    @Query("delete from categoriesTable where id=:category_id ")
    fun deleteNotesByCategoryId(category_id: Int)
}