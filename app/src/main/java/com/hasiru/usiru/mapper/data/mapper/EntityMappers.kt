package com.hasiru.usiru.mapper.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hasiru.usiru.mapper.data.local.entity.PitEntity
import com.hasiru.usiru.mapper.data.local.entity.SpeciesEntity
import com.hasiru.usiru.mapper.data.local.entity.TreeEntity
import com.hasiru.usiru.mapper.domain.model.*

private val gson = Gson()

fun TreeEntity.toDomain() = TreeMarker(
    id = id,
    userId = userId,
    userName = userName,
    latitude = latitude,
    longitude = longitude,
    species = species,
    speciesKn = speciesKn,
    scientificName = scientificName,
    girthCm = girthCm,
    healthCondition = HealthCondition.valueOf(healthCondition),
    ageEstimateYears = ageEstimateYears,
    oxygenScore = oxygenScore,
    imageUrl = imageUrl,
    environmentalBenefits = environmentalBenefits,
    kannadaDescription = kannadaDescription,
    healthSuggestions = healthSuggestions,
    aiConfidence = aiConfidence,
    status = ReportStatus.valueOf(status),
    city = city,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncStatus = SyncStatus.valueOf(syncStatus)
)

fun TreeMarker.toEntity(localImagePath: String? = null) = TreeEntity(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    userId = userId,
    userName = userName,
    latitude = latitude,
    longitude = longitude,
    species = species,
    speciesKn = speciesKn,
    scientificName = scientificName,
    girthCm = girthCm,
    healthCondition = healthCondition.name,
    ageEstimateYears = ageEstimateYears,
    oxygenScore = oxygenScore,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    environmentalBenefits = environmentalBenefits,
    kannadaDescription = kannadaDescription,
    healthSuggestions = healthSuggestions,
    aiConfidence = aiConfidence,
    status = status.name,
    city = city,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncStatus = syncStatus.name
)

fun PitEntity.toDomain(): EmptyPit {
    val type = object : TypeToken<List<String>>() {}.type
    val species: List<String> = try {
        gson.fromJson(recommendedSpeciesJson, type) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
    return EmptyPit(
        id = id,
        userId = userId,
        userName = userName,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        soilType = SoilType.valueOf(soilType),
        pitWidthCm = pitWidthCm,
        pitDepthCm = pitDepthCm,
        waterAvailability = WaterAvailability.valueOf(waterAvailability),
        sunlightExposure = SunlightExposure.valueOf(sunlightExposure),
        nearbyConditions = nearbyConditions,
        recommendedSpecies = species,
        priority = PitPriority.valueOf(priority),
        status = ReportStatus.valueOf(status),
        adminNotes = adminNotes,
        city = city,
        createdAt = createdAt,
        syncStatus = SyncStatus.valueOf(syncStatus)
    )
}

fun EmptyPit.toEntity(localImagePath: String? = null) = PitEntity(
    id = id.ifEmpty { java.util.UUID.randomUUID().toString() },
    userId = userId,
    userName = userName,
    latitude = latitude,
    longitude = longitude,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    soilType = soilType.name,
    pitWidthCm = pitWidthCm,
    pitDepthCm = pitDepthCm,
    waterAvailability = waterAvailability.name,
    sunlightExposure = sunlightExposure.name,
    nearbyConditions = nearbyConditions,
    recommendedSpeciesJson = gson.toJson(recommendedSpecies),
    priority = priority.name,
    status = status.name,
    adminNotes = adminNotes,
    city = city,
    createdAt = createdAt,
    syncStatus = syncStatus.name
)

fun SpeciesEntity.toDomain() = SpeciesInfo(
    id = id,
    nameEn = nameEn,
    nameKn = nameKn,
    scientificName = scientificName,
    imageUrl = imageUrl,
    oxygenFactor = oxygenFactor,
    medicinalUses = medicinalUses,
    oxygenBenefits = oxygenBenefits,
    lifespanYears = lifespanYears,
    waterRequirement = waterRequirement,
    plantingSeason = plantingSeason,
    environmentalImportance = environmentalImportance,
    culturalSignificance = culturalSignificance,
    descriptionEn = descriptionEn,
    descriptionKn = descriptionKn,
    isFavorite = isFavorite
)

fun SpeciesInfo.toEntity() = SpeciesEntity(
    id = id,
    nameEn = nameEn,
    nameKn = nameKn,
    scientificName = scientificName,
    imageUrl = imageUrl,
    oxygenFactor = oxygenFactor,
    medicinalUses = medicinalUses,
    oxygenBenefits = oxygenBenefits,
    lifespanYears = lifespanYears,
    waterRequirement = waterRequirement,
    plantingSeason = plantingSeason,
    environmentalImportance = environmentalImportance,
    culturalSignificance = culturalSignificance,
    descriptionEn = descriptionEn,
    descriptionKn = descriptionKn,
    isFavorite = isFavorite
)
