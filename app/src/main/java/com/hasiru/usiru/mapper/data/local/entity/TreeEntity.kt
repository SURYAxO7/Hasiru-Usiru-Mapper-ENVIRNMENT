package com.hasiru.usiru.mapper.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trees")
data class TreeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val species: String,
    val speciesKn: String,
    val scientificName: String,
    val girthCm: Double,
    val healthCondition: String,
    val ageEstimateYears: Int,
    val oxygenScore: Double,
    val imageUrl: String,
    val localImagePath: String?,
    val environmentalBenefits: String,
    val kannadaDescription: String,
    val healthSuggestions: String,
    val aiConfidence: Float,
    val status: String,
    val city: String,
    val createdAt: Long,
    val updatedAt: Long,
    val syncStatus: String
)

@Entity(tableName = "empty_pits")
data class PitEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val localImagePath: String?,
    val soilType: String,
    val pitWidthCm: Double,
    val pitDepthCm: Double,
    val waterAvailability: String,
    val sunlightExposure: String,
    val nearbyConditions: String,
    val recommendedSpeciesJson: String,
    val priority: String,
    val status: String,
    val adminNotes: String,
    val city: String,
    val createdAt: Long,
    val syncStatus: String
)

@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameKn: String,
    val scientificName: String,
    val imageUrl: String,
    val oxygenFactor: Double,
    val medicinalUses: String,
    val oxygenBenefits: String,
    val lifespanYears: Int,
    val waterRequirement: String,
    val plantingSeason: String,
    val environmentalImportance: String,
    val culturalSignificance: String,
    val descriptionEn: String,
    val descriptionKn: String,
    val isFavorite: Boolean
)

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,
    val entityId: String,
    val payloadJson: String,
    val imagePath: String?,
    val createdAt: Long,
    val retryCount: Int = 0
)
