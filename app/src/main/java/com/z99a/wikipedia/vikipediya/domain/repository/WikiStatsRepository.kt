package com.z99a.wikipedia.vikipediya.domain.repository

import com.z99a.wikipedia.vikipediya.domain.service.WikipediaStats

interface WikiStatsRepository {
    suspend fun getStats(): WikipediaStats
}