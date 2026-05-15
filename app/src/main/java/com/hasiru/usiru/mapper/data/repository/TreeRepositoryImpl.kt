package com.hasiru.usiru.mapper.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.hasiru.usiru.mapper.core.network.NetworkMonitor
import com.hasiru.usiru.mapper.data.local.HasiruDatabase
import com.hasiru.usiru.mapper.data.mapper.toDomain
import com.hasiru.usiru.mapper.data.mapper.toEntity
import com.hasiru.usiru.mapper.data.remote.firebase.FirestorePaths
import com.hasiru.usiru.mapper.data.remote.firebase.toFirestoreMap
import com.hasiru.usiru.mapper.data.remote.firebase.toTreeMarker
import com.hasiru.usiru.mapper.domain.engine.OxygenScoreEngine
import com.hasiru.usiru.mapper.domain.model.SyncStatus
import com.hasiru.usiru.mapper.domain.model.TreeMarker
import com.hasiru.usiru.mapper.domain.repository.TreeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TreeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val database: HasiruDatabase,
    private val networkMonitor: NetworkMonitor,
    private val oxygenScoreEngine: OxygenScoreEngine
) : TreeRepository {

    private var listener: ListenerRegistration? = null

    override fun observeTrees(city: String?): Flow<List<TreeMarker>> = callbackFlow {
        val query = if (city.isNullOrBlank()) {
            firestore.collection(FirestorePaths.TREES)
        } else {
            firestore.collection(FirestorePaths.TREES).whereEqualTo("city", city)
        }
        listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val trees = snapshot?.documents?.mapNotNull { it.toTreeMarker() } ?: emptyList()
            kotlinx.coroutines.runBlocking {
                database.treeDao().insertAll(trees.map { it.toEntity() })
            }
            trySend(trees)
        }
        awaitClose { listener?.remove() }
    }

    override fun observeTree(id: String): Flow<TreeMarker?> =
        database.treeDao().observeById(id).map { it?.toDomain() }

    override suspend fun getTrees(city: String?): List<TreeMarker> {
        val query = if (city.isNullOrBlank()) {
            firestore.collection(FirestorePaths.TREES).get().await()
        } else {
            firestore.collection(FirestorePaths.TREES).whereEqualTo("city", city).get().await()
        }
        return query.documents.mapNotNull { it.toTreeMarker() }
    }

    override suspend fun saveTree(tree: TreeMarker, imageBytes: ByteArray?): Result<TreeMarker> {
        val id = tree.id.ifEmpty { UUID.randomUUID().toString() }
        var updated = oxygenScoreEngine.recalculateTree(tree.copy(id = id))
        
        try {
            if (networkMonitor.isOnline()) {
                // Wrap Firebase in a timeout to prevent UI hang if API is disabled or blocked
                kotlinx.coroutines.withTimeout(5000) {
                    val imageUrl = imageBytes?.let { uploadImage(id, it) } ?: tree.imageUrl
                    updated = updated.copy(
                        imageUrl = imageUrl,
                        syncStatus = SyncStatus.SYNCED,
                        updatedAt = System.currentTimeMillis()
                    )
                    firestore.collection(FirestorePaths.TREES)
                        .document(id)
                        .set(updated.toFirestoreMap())
                        .await()
                    incrementUserStats(updated.userId, trees = 1)
                }
            } else {
                updated = updated.copy(syncStatus = SyncStatus.PENDING_UPLOAD)
            }
        } catch (_: Exception) {
            // Fallback to local save if Firebase fails or timeouts
            updated = updated.copy(syncStatus = SyncStatus.PENDING_UPLOAD)
        }
        
        database.treeDao().insert(updated.toEntity())
        return Result.success(updated)
    }

    override suspend fun updateTree(tree: TreeMarker): Result<Unit> = try {
        val updated = oxygenScoreEngine.recalculateTree(tree)
        if (networkMonitor.isOnline()) {
            firestore.collection(FirestorePaths.TREES)
                .document(updated.id)
                .set(updated.toFirestoreMap())
                .await()
        }
        database.treeDao().insert(updated.toEntity())
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTree(id: String): Result<Unit> = try {
        if (networkMonitor.isOnline()) {
            firestore.collection(FirestorePaths.TREES).document(id).delete().await()
        }
        database.treeDao().delete(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun syncPending(): Result<Int> {
        if (!networkMonitor.isOnline()) return Result.success(0)
        val pending = database.treeDao().getPending()
        var synced = 0
        pending.forEach { entity ->
            val tree = entity.toDomain()
            try {
                firestore.collection(FirestorePaths.TREES)
                    .document(tree.id)
                    .set(tree.copy(syncStatus = SyncStatus.SYNCED).toFirestoreMap())
                    .await()
                database.treeDao().insert(
                    tree.copy(syncStatus = SyncStatus.SYNCED).toEntity(entity.localImagePath)
                )
                synced++
            } catch (_: Exception) { }
        }
        return Result.success(synced)
    }

    private suspend fun uploadImage(treeId: String, bytes: ByteArray): String {
        val ref = storage.reference.child("trees/$treeId/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    private suspend fun incrementUserStats(userId: String, trees: Int = 0) {
        if (userId.isBlank()) return
        val userRef = firestore.collection(FirestorePaths.USERS).document(userId)
        firestore.runTransaction { tx ->
            val snap = tx.get(userRef)
            val currentTrees = snap.getLong("treesTagged")?.toInt() ?: 0
            val points = snap.getLong("contributionPoints")?.toInt() ?: 0
            tx.update(
                userRef,
                mapOf(
                    "treesTagged" to currentTrees + trees,
                    "contributionPoints" to points + (trees * 10)
                )
            )
        }.await()
    }
}
