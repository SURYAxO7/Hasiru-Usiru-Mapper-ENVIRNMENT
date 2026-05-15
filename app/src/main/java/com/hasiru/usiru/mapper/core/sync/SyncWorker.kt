package com.hasiru.usiru.mapper.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val treeRepository: TreeRepository,
    private val pitRepository: PitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val trees = treeRepository.syncPending().getOrDefault(0)
        val pits = pitRepository.syncPending().getOrDefault(0)
        return if (trees >= 0 && pits >= 0) Result.success() else Result.retry()
    }
}
