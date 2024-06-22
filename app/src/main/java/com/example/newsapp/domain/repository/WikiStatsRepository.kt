package com.example.newsapp.domain.repository
import com.example.newsapp.domain.service.WikipediaStats

interface WikiStatsRepository {
    suspend fun getStats(): WikipediaStats
}