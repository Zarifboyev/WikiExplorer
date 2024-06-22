package com.example.newsapp.domain.impl

import com.example.newsapp.domain.repository.WikiStatsRepository
import com.example.newsapp.domain.service.WikipediaApiService
import com.example.newsapp.domain.service.WikipediaStats
import javax.inject.Inject

class WikiStatsRepositoryImpl @Inject constructor(
    private val api: WikipediaApiService
) : WikiStatsRepository {
    override suspend fun getStats(): WikipediaStats {

        val response = api.getStats()
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