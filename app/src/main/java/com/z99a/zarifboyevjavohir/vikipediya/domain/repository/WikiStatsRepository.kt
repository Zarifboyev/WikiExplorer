package com.z99a.zarifboyevjavohir.vikipediya.domain.repository

import com.z99a.zarifboyevjavohir.vikipediya.domain.service.WikipediaStats

interface WikiStatsRepository {
    suspend fun getStats(): WikipediaStats
}