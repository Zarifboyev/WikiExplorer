package com.z99a.zarifboyevjavohir.vikipediya.data.entity

import androidx.room.Entity
    import androidx.room.PrimaryKey
    import java.io.Serializable

    @Entity(tableName = "wiki_articles")
    data class WikiModel(
        @PrimaryKey(autoGenerate = true)
        val pageid: Int,
        val ns: Int,
        val title: String
    ):Serializable