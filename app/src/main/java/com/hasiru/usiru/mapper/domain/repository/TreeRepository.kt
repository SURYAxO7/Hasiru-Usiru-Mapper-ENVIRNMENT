package com.hasiru.usiru.mapper.domain.repository

import com.hasiru.usiru.mapper.domain.model.TreeMarker
import kotlinx.coroutines.flow.Flow

interface TreeRepository {
    fun observeTrees(city: String? = null): Flow<List<TreeMarker>>
    fun observeTree(id: String): Flow<TreeMarker?>
    suspend fun getTrees(city: String? = null): List<TreeMarker>
    suspend fun saveTree(tree: TreeMarker, imageBytes: ByteArray?): Result<TreeMarker>
    suspend fun updateTree(tree: TreeMarker): Result<Unit>
    suspend fun deleteTree(id: String): Result<Unit>
    suspend fun syncPending(): Result<Int>
}
