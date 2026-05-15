package com.hasiru.usiru.mapper.domain.model

data class EmptyPit(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val soilType: SoilType = SoilType.LOAMY,
    val pitWidthCm: Double = 0.0,
    val pitDepthCm: Double = 0.0,
    val waterAvailability: WaterAvailability = WaterAvailability.MODERATE,
    val sunlightExposure: SunlightExposure = SunlightExposure.PARTIAL,
    val nearbyConditions: String = "",
    val recommendedSpecies: List<String> = emptyList(),
    val priority: PitPriority = PitPriority.NORMAL,
    val status: ReportStatus = ReportStatus.PENDING,
    val adminNotes: String = "",
    val city: String = "Bengaluru",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class SoilType { CLAY, SANDY, LOAMY, ROCKY, RED_SOIL }
enum class WaterAvailability { LOW, MODERATE, HIGH }
enum class SunlightExposure { FULL, PARTIAL, SHADE }
enum class PitPriority { LOW, NORMAL, HIGH, URGENT }
