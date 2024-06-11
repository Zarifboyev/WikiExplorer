package com.example.newsapp.domain.service

import com.example.newsapp.data.entity.WikiModel
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki
import timber.log.Timber
import javax.inject.Inject

class WikiService @Inject constructor(){

    private val DOMAIN = "uz.wikipedia.org"
    private val TEST_IMAGE_URL = "https://cdn.pixabay.com/photo/2021/09/13/08/16/purple-flower-6620617_1280.jpg"

    private var build:Wiki.Builder = Wiki.Builder()
    fun fetchArticles(builder: Wiki.Builder): List<WikiModel> {
        val wiki: Wiki = builder.withDomain(DOMAIN).build()

        build = builder
        val pages = ArrayList<WikiModel>()

        val randomPages = wiki.getRandomPages(10, NS.MAIN)
        var index_i = 0
        randomPages.forEachIndexed { index, item ->
            val imagesList = wiki.getImagesOnPage(item)
            if (imagesList.isNotEmpty()) {
                val image = TEST_IMAGE_URL
                Timber.tag("image").d(image)
                val description = wiki.getTextExtract(item) ?: ""
                val model = WikiModel(index_i, item, description, image)
                index_i++
                pages.add(model)
            }
        }
        return pages
    }

    fun removeFilePrefix(fileName: String): String {
        if (fileName.startsWith("File:")) {
            return fileName.substring("File:".length)
        } else {
            return fileName
        }
    }
}
