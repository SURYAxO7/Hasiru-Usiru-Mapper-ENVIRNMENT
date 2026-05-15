package com.hasiru.usiru.mapper.domain.repository

import com.hasiru.usiru.mapper.domain.model.SpeciesInfo
import kotlinx.coroutines.flow.Flow

interface SpeciesRepository {
    fun observeSpecies(): Flow<List<SpeciesInfo>>
    suspend fun searchSpecies(query: String): List<SpeciesInfo>
    suspend fun toggleFavorite(id: String): Result<Unit>
    suspend fun seedDefaultSpecies()
}
