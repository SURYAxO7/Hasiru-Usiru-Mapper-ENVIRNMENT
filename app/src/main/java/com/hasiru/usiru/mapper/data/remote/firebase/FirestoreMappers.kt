package com.hasiru.usiru.mapper.data.remote.firebase

import com.hasiru.usiru.mapper.domain.model.*
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toTreeMarker(): TreeMarker? {
    if (!exists()) return null
    return TreeMarker(
        id = id,
        userId = getString("userId") ?: "",
        userName = getString("userName") ?: "",
        latitude = getDouble("latitude") ?: 0.0,
        longitude = getDouble("longitude") ?: 0.0,
        species = getString("species") ?: "",
        speciesKn = getString("speciesKn") ?: "",
        scientificName = getString("scientificName") ?: "",
        girthCm = getDouble("girthCm") ?: 0.0,
        healthCondition = try {
            HealthCondition.valueOf(getString("healthCondition") ?: "GOOD")
        } catch (_: Exception) { HealthCondition.GOOD },
        ageEstimateYears = getLong("ageEstimateYears")?.toInt() ?: 0,
        oxygenScore = getDouble("oxygenScore") ?: 0.0,
        imageUrl = getString("imageUrl") ?: "",
        environmentalBenefits = getString("environmentalBenefits") ?: "",
        kannadaDescription = getString("kannadaDescription") ?: "",
        healthSuggestions = getString("healthSuggestions") ?: "",
        aiConfidence = (getDouble("aiConfidence") ?: 0.0).toFloat(),
        status = try {
            ReportStatus.valueOf(getString("status") ?: "PENDING")
        } catch (_: Exception) { ReportStatus.PENDING },
        city = getString("city") ?: "Bengaluru",
        createdAt = getLong("createdAt") ?: 0L,
        updatedAt = getLong("updatedAt") ?: 0L
    )
}

fun TreeMarker.toFirestoreMap() = mapOf(
    "userId" to userId,
    "userName" to userName,
    "latitude" to latitude,
    "longitude" to longitude,
    "species" to species,
    "speciesKn" to speciesKn,
    "scientificName" to scientificName,
    "girthCm" to girthCm,
    "healthCondition" to healthCondition.name,
    "ageEstimateYears" to ageEstimateYears,
    "oxygenScore" to oxygenScore,
    "imageUrl" to imageUrl,
    "environmentalBenefits" to environmentalBenefits,
    "kannadaDescription" to kannadaDescription,
    "healthSuggestions" to healthSuggestions,
    "aiConfidence" to aiConfidence,
    "status" to status.name,
    "city" to city,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

fun DocumentSnapshot.toEmptyPit(): EmptyPit? {
    if (!exists()) return null
    @Suppress("UNCHECKED_CAST")
    val recommended = (get("recommendedSpecies") as? List<String>) ?: emptyList()
    return EmptyPit(
        id = id,
        userId = getString("userId") ?: "",
        userName = getString("userName") ?: "",
        latitude = getDouble("latitude") ?: 0.0,
        longitude = getDouble("longitude") ?: 0.0,
        imageUrl = getString("imageUrl") ?: "",
        soilType = try {
            SoilType.valueOf(getString("soilType") ?: "LOAMY")
        } catch (_: Exception) { SoilType.LOAMY },
        pitWidthCm = getDouble("pitWidthCm") ?: 0.0,
        pitDepthCm = getDouble("pitDepthCm") ?: 0.0,
        waterAvailability = try {
            WaterAvailability.valueOf(getString("waterAvailability") ?: "MODERATE")
        } catch (_: Exception) { WaterAvailability.MODERATE },
        sunlightExposure = try {
            SunlightExposure.valueOf(getString("sunlightExposure") ?: "PARTIAL")
        } catch (_: Exception) { SunlightExposure.PARTIAL },
        nearbyConditions = getString("nearbyConditions") ?: "",
        recommendedSpecies = recommended,
        priority = try {
            PitPriority.valueOf(getString("priority") ?: "NORMAL")
        } catch (_: Exception) { PitPriority.NORMAL },
        status = try {
            ReportStatus.valueOf(getString("status") ?: "PENDING")
        } catch (_: Exception) { ReportStatus.PENDING },
        adminNotes = getString("adminNotes") ?: "",
        city = getString("city") ?: "Bengaluru",
        createdAt = getLong("createdAt") ?: 0L
    )
}

fun EmptyPit.toFirestoreMap() = mapOf(
    "userId" to userId,
    "userName" to userName,
    "latitude" to latitude,
    "longitude" to longitude,
    "imageUrl" to imageUrl,
    "soilType" to soilType.name,
    "pitWidthCm" to pitWidthCm,
    "pitDepthCm" to pitDepthCm,
    "waterAvailability" to waterAvailability.name,
    "sunlightExposure" to sunlightExposure.name,
    "nearbyConditions" to nearbyConditions,
    "recommendedSpecies" to recommendedSpecies,
    "priority" to priority.name,
    "status" to status.name,
    "adminNotes" to adminNotes,
    "city" to city,
    "createdAt" to createdAt
)

fun UserProfile.toFirestoreMap() = mapOf(
    "name" to name,
    "email" to email,
    "phone" to phone,
    "photoUrl" to photoUrl,
    "role" to role.name,
    "contributionPoints" to contributionPoints,
    "treesTagged" to treesTagged,
    "pitsReported" to pitsReported,
    "badges" to badges,
    "preferredLanguage" to preferredLanguage,
    "city" to city,
    "createdAt" to createdAt
)

fun DocumentSnapshot.toUserProfile(uid: String) = UserProfile(
    uid = uid,
    name = getString("name") ?: "",
    email = getString("email") ?: "",
    phone = getString("phone") ?: "",
    photoUrl = getString("photoUrl") ?: "",
    role = try {
        UserRole.valueOf(getString("role") ?: "CITIZEN")
    } catch (_: Exception) { UserRole.CITIZEN },
    contributionPoints = getLong("contributionPoints")?.toInt() ?: 0,
    treesTagged = getLong("treesTagged")?.toInt() ?: 0,
    pitsReported = getLong("pitsReported")?.toInt() ?: 0,
    badges = (get("badges") as? List<String>) ?: emptyList(),
    preferredLanguage = getString("preferredLanguage") ?: "en",
    city = getString("city") ?: "Bengaluru",
    createdAt = getLong("createdAt") ?: 0L
)
