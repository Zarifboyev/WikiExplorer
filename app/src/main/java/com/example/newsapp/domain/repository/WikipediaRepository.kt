package com.example.newsapp.domain.repository

import com.example.newsapp.domain.service.RedLinksResponse
import com.example.newsapp.domain.service.SectionsResponse
import com.example.newsapp.domain.service.WikipediaApiService
import com.example.newsapp.domain.service.WikipediaStats
import retrofit2.Response
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val apiService: WikipediaApiService
) {

    suspend fun getPageSections(page: String): Response<SectionsResponse> {
        return apiService.getPageSections(page = page)
    }

    suspend fun getRedLinks(title: String): Response<RedLinksResponse> {
        return apiService.getRedLinks(titles = title)
    }
    suspend fun getStats(): WikipediaStats {
        val response = apiService.getStats()
        if (response.isSuccessful) {
            response.body()?.let {
                val stats = it.query.statistics
                return WikipediaStats(
                    articles = stats.articles,
                    activeUsers = stats.activeusers,
                    edits = stats.edits
                )
            }
        }
        throw Exception("Failed to fetch stats")
    }
}
