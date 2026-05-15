package com.hasiru.usiru.mapper.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hasiru.usiru.mapper.core.network.NetworkMonitor
import com.hasiru.usiru.mapper.data.local.HasiruDatabase
import com.hasiru.usiru.mapper.data.mapper.toDomain
import com.hasiru.usiru.mapper.data.mapper.toEntity
import com.hasiru.usiru.mapper.data.remote.firebase.FirestorePaths
import com.hasiru.usiru.mapper.data.remote.firebase.toEmptyPit
import com.hasiru.usiru.mapper.data.remote.firebase.toFirestoreMap
import com.hasiru.usiru.mapper.domain.ai.SpeciesIdentificationService
import com.hasiru.usiru.mapper.domain.model.EmptyPit
import com.hasiru.usiru.mapper.domain.model.ReportStatus
import com.hasiru.usiru.mapper.domain.model.SyncStatus
import com.hasiru.usiru.mapper.domain.repository.PitRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PitRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val database: HasiruDatabase,
    private val networkMonitor: NetworkMonitor,
    private val aiService: SpeciesIdentificationService
) : PitRepository {

    override fun observePits(city: String?): Flow<List<EmptyPit>> = callbackFlow {
        val query = if (city.isNullOrBlank()) {
            firestore.collection(FirestorePaths.PITS)
        } else {
            firestore.collection(FirestorePaths.PITS).whereEqualTo("city", city)
        }
        val reg = query.addSnapshotListener { snapshot, _ ->
            val pits = snapshot?.documents?.mapNotNull { it.toEmptyPit() } ?: emptyList()
            kotlinx.coroutines.runBlocking {
                database.pitDao().insertAll(pits.map { it.toEntity() })
            }
            trySend(pits)
        }
        awaitClose { reg.remove() }
    }

    override suspend fun savePit(pit: EmptyPit, imageBytes: ByteArray?): Result<EmptyPit> {
        val id = pit.id.ifEmpty { UUID.randomUUID().toString() }
        val recommended = if (pit.recommendedSpecies.isEmpty()) {
            aiService.recommendSpeciesForPit(
                pit.soilType.name,
                pit.waterAvailability.name,
                pit.sunlightExposure.name,
                pit.city
            )
        } else pit.recommendedSpecies

        var updated = pit.copy(id = id, recommendedSpecies = recommended)
        
        try {
            if (networkMonitor.isOnline()) {
                kotlinx.coroutines.withTimeout(5000) {
                    val imageUrl = imageBytes?.let { uploadPitImage(id, it) } ?: pit.imageUrl
                    updated = updated.copy(imageUrl = imageUrl, syncStatus = SyncStatus.SYNCED)
                    firestore.collection(FirestorePaths.PITS).document(id)
                        .set(updated.toFirestoreMap()).await()
                }
            } else {
                updated = updated.copy(syncStatus = SyncStatus.PENDING_UPLOAD)
            }
        } catch (_: Exception) {
            updated = updated.copy(syncStatus = SyncStatus.PENDING_UPLOAD)
        }
        
        database.pitDao().insert(updated.toEntity())
        return Result.success(updated)
    }

    override suspend fun updatePitStatus(
        id: String,
        status: ReportStatus,
        notes: String
    ): Result<Unit> = try {
        firestore.collection(FirestorePaths.PITS).document(id)
            .update(mapOf("status" to status.name, "adminNotes" to notes))
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun syncPending(): Result<Int> {
        if (!networkMonitor.isOnline()) return Result.success(0)
        val pending = database.pitDao().getPending()
        var synced = 0
        pending.forEach { entity ->
            try {
                val pit = entity.toDomain()
                firestore.collection(FirestorePaths.PITS).document(pit.id)
                    .set(pit.copy(syncStatus = SyncStatus.SYNCED).toFirestoreMap()).await()
                synced++
            } catch (_: Exception) { }
        }
        return Result.success(synced)
    }

    private suspend fun uploadPitImage(pitId: String, bytes: ByteArray): String {
        val ref = storage.reference.child("pits/$pitId/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }
}
