package com.example.newsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "wiki_entities")

data class WikiModel(
    @PrimaryKey(autoGenerate = true)

    val id: Int,
    val title: String,
    val description: String,
    val image: String
):Serializable

