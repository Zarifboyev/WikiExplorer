package com.example.newsapp.domain.impl

import com.example.newsapp.data.entity.WikiEntity
import kotlin.random.Random


class WikiService {

    fun fetchArticle(title: String) {
        //val page = wiki.getPageText(title)
       // val image = wiki.getImagesOnPage(title)[0]
        //return if (page.isNullOrEmpty()) null else WikiEntity(id = Random.nextInt(), title = title, description = page, image = image)
        return Unit
    }
}
