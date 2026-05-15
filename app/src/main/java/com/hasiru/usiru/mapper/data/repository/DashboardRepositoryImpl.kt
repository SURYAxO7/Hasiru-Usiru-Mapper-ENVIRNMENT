package com.hasiru.usiru.mapper.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hasiru.usiru.mapper.data.remote.firebase.FirestorePaths
import com.hasiru.usiru.mapper.data.remote.firebase.toEmptyPit
import com.hasiru.usiru.mapper.data.remote.firebase.toTreeMarker
import com.hasiru.usiru.mapper.domain.engine.OxygenScoreEngine
import com.hasiru.usiru.mapper.domain.model.ActivityItem
import com.hasiru.usiru.mapper.domain.model.DashboardStats
import com.hasiru.usiru.mapper.domain.model.LeaderboardEntry
import com.hasiru.usiru.mapper.domain.model.ReportStatus
import com.hasiru.usiru.mapper.domain.repository.DashboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val oxygenScoreEngine: OxygenScoreEngine
) : DashboardRepository {

    override fun observeStats(city: String): Flow<DashboardStats> = callbackFlow {
        val treesReg = firestore.collection(FirestorePaths.TREES)
            .whereEqualTo("city", city)
            .addSnapshotListener { treeSnap, _ ->
                val pitsReg = firestore.collection(FirestorePaths.PITS)
                    .whereEqualTo("city", city)
                    .addSnapshotListener { pitSnap, _ ->
                        val trees = treeSnap?.documents?.mapNotNull { it.toTreeMarker() } ?: emptyList()
                        val pits = pitSnap?.documents?.mapNotNull { it.toEmptyPit() } ?: emptyList()
                        val approved = trees.filter { it.status == ReportStatus.APPROVED }
                        val oxygen = oxygenScoreEngine.communityScore(approved)
                        val activities = trees.take(5).map {
                            ActivityItem(
                                id = it.id,
                                title = "${it.species} tagged",
                                description = "by ${it.userName}",
                                timestamp = it.createdAt,
                                type = "tree"
                            )
                        }
                        trySend(
                            DashboardStats(
                                totalTrees = trees.size,
                                totalEmptyPits = pits.size,
                                communityOxygenScore = oxygen,
                                userContributions = trees.size + pits.size,
                                weeklyTrend = generateWeeklyTrend(approved.map { it.oxygenScore }),
                                recentActivities = activities
                            )
                        )
                    }
            }
        awaitClose {
            treesReg.remove()
        }
    }

    override fun observeLeaderboard(city: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        val reg = firestore.collection(FirestorePaths.USERS)
            .whereEqualTo("city", city)
            .orderBy("contributionPoints", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, _ ->
                val entries = snapshot?.documents?.mapIndexed { index, doc ->
                    LeaderboardEntry(
                        userId = doc.id,
                        userName = doc.getString("name") ?: "User",
                        photoUrl = doc.getString("photoUrl") ?: "",
                        points = doc.getLong("contributionPoints")?.toInt() ?: 0,
                        treesTagged = doc.getLong("treesTagged")?.toInt() ?: 0,
                        rank = index + 1
                    )
                } ?: emptyList()
                trySend(entries)
            }
        awaitClose { reg.remove() }
    }

    private fun generateWeeklyTrend(scores: List<Double>): List<Double> {
        if (scores.isEmpty()) return List(7) { 0.0 }
        val chunk = scores.size / 7.coerceAtLeast(1)
        return (0 until 7).map { i ->
            scores.drop(i * chunk).take(chunk).sum()
        }
    }
}
