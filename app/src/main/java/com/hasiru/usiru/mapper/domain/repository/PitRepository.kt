package com.hasiru.usiru.mapper.domain.repository

import com.hasiru.usiru.mapper.domain.model.EmptyPit
import kotlinx.coroutines.flow.Flow

interface PitRepository {
    fun observePits(city: String? = null): Flow<List<EmptyPit>>
    suspend fun savePit(pit: EmptyPit, imageBytes: ByteArray?): Result<EmptyPit>
    suspend fun updatePitStatus(id: String, status: com.hasiru.usiru.mapper.domain.model.ReportStatus, notes: String): Result<Unit>
    suspend fun syncPending(): Result<Int>
}
