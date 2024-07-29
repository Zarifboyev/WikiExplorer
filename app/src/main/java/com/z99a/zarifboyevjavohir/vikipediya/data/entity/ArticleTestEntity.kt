package com.z99a.zarifboyevjavohir.vikipediya.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articlesTable",
    foreignKeys = [ForeignKey(
        entity = WikiModel::class,
        childColumns = ["article_id"],
        parentColumns = ["pageid"],
        onDelete = ForeignKey.CASCADE,
    )]
)

data class ArticleTestEntity(
    @PrimaryKey(autoGenerate = true)
    val article_id: Int,
    val article_wiki_text: String,
    val title: String,
) : Serializable