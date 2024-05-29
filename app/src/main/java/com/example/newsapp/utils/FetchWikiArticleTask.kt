package com.example.newsapp.utils

import android.os.AsyncTask
import android.util.Log
import com.example.newsapp.model.WikiNews
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils
import io.github.fastily.jwiki.core.NS
import io.github.fastily.jwiki.core.Wiki

class FetchWikiArticleTask(private val listener: FetchWikiArticleListener?) :
    AsyncTask<Void, Void, List<WikiNews>>() {

    interface FetchWikiArticleListener {
        fun onWikiArticleFetched(htmlContent: String?)

        fun onWikiArticlesFetched(wikiNewsList: List<WikiNews>?)
    }

    override fun doInBackground(vararg voids: Void?): List<WikiNews> {
        return fetchWikiArticles()
    }

    override fun onPostExecute(wikiNewsList: List<WikiNews>) {
        super.onPostExecute(wikiNewsList)
        listener?.onWikiArticlesFetched(wikiNewsList)
    }

    companion object {
        private fun getImageUrl(fileName: String): String {
            val baseUrl = "https://upload.wikimedia.org/wikipedia/commons/"
            val md5Hash = DigestUtils.md5Hex(fileName.replace("File:", ""))
            val urlPath = String.format(
                "%s/%s/%s",
                md5Hash.substring(0, 1),
                md5Hash.substring(0, 2),
                fileName.replace("File:", "").replace(" ", "_")
            )
            Log.d("Image URL", urlPath)
            return baseUrl + urlPath
        }

        fun fetchWikiArticles(): List<WikiNews> {
            val wikiNewsList = mutableListOf<WikiNews>()
            val wiki = Wiki.Builder().build()

            val randomPages: List<String> = wiki.getRandomPages(10, NS.MAIN) // Fetch 10 random pages
            for (title in randomPages) {
                // Getting page text and image URL
                val imageInfos: List<String> = wiki.getImagesOnPage(title)
                if (imageInfos.isNotEmpty()) {
                    // Extract the first image file name from the page
                    val fileName = FileNameExtractor.extractFileName(imageInfos[0])
                    // Generate URL for the image
                    val imageUrl = getImageUrl(fileName)
                    Log.d("Image URL", imageUrl)

                    // Fetch page text
                    val pageText = wiki.getTextExtract(title).replace("\\(.*?\\)".toRegex(), "")

                    // Generate Wikipedia URL for the page
                    val url = "https://en.wikipedia.org/wiki/$title"

                    // Add the news to the list
                    wikiNewsList.add(WikiNews(title, pageText, url, imageUrl))
                }
            }

            return wikiNewsList
        }
    }
}
