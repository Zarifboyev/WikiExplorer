package com.example.newsapp.domain.impl

import android.content.Context
import android.util.Log
import com.example.newsapp.data.model.WikiModel
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki

class WikiService {

    fun fetchArticles(context: Context, builder: Wiki.Builder): List<WikiModel> {
        val wiki = builder.build()
        val pages = ArrayList<WikiModel>()
        val randomPages = wiki.getRandomPages(5, NS.MAIN)

        randomPages.forEachIndexed { index, item ->
            val imagesList = wiki.getImagesOnPage(item)
            if (imagesList.isNotEmpty()) {
                val image = WikimediaCommonsURLGenerator.generateFileURL(imagesList[0])
                Log.d("image", image)
                val description = wiki.getTextExtract(item) ?: ""
                val model = WikiModel(index, item, description, image)
                pages.add(model)
            }
        }
        return pages
    }
    fun removeFilePrefix(fileName: String): String {
        return if (fileName.startsWith("File:")) {
            fileName.substring("File:".length)
        } else {
            fileName
        }
    }
}
