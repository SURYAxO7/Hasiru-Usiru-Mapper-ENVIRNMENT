package com.hasiru.usiru.mapper.domain.model

data class TreeMarker(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val species: String = "",
    val speciesKn: String = "",
    val scientificName: String = "",
    val girthCm: Double = 0.0,
    val healthCondition: HealthCondition = HealthCondition.GOOD,
    val ageEstimateYears: Int = 0,
    val oxygenScore: Double = 0.0,
    val imageUrl: String = "",
    val environmentalBenefits: String = "",
    val kannadaDescription: String = "",
    val healthSuggestions: String = "",
    val aiConfidence: Float = 0f,
    val status: ReportStatus = ReportStatus.PENDING,
    val city: String = "Bengaluru",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class HealthCondition { EXCELLENT, GOOD, FAIR, POOR, CRITICAL }
enum class ReportStatus { PENDING, APPROVED, REJECTED }
enum class SyncStatus { SYNCED, PENDING_UPLOAD, FAILED }
