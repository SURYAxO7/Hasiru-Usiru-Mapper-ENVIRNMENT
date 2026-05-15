package com.hasiru.usiru.mapper.domain.repository

import com.hasiru.usiru.mapper.domain.model.DashboardStats
import com.hasiru.usiru.mapper.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeStats(city: String): Flow<DashboardStats>
    fun observeLeaderboard(city: String): Flow<List<LeaderboardEntry>>
}
