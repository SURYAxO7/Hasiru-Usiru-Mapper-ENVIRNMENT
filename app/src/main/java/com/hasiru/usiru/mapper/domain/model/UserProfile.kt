package com.hasiru.usiru.mapper.domain.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val role: UserRole = UserRole.CITIZEN,
    val contributionPoints: Int = 0,
    val treesTagged: Int = 0,
    val pitsReported: Int = 0,
    val badges: List<String> = emptyList(),
    val preferredLanguage: String = "en",
    val city: String = "Bengaluru",
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole { CITIZEN, VOLUNTEER, MUNICIPALITY, ADMIN }

data class DashboardStats(
    val totalTrees: Int = 0,
    val totalEmptyPits: Int = 0,
    val communityOxygenScore: Double = 0.0,
    val userContributions: Int = 0,
    val weeklyTrend: List<Double> = emptyList(),
    val recentActivities: List<ActivityItem> = emptyList()
)

data class ActivityItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val type: String = "tree"
)

data class SpeciesInfo(
    val id: String = "",
    val nameEn: String = "",
    val nameKn: String = "",
    val scientificName: String = "",
    val imageUrl: String = "",
    val oxygenFactor: Double = 1.0,
    val medicinalUses: String = "",
    val oxygenBenefits: String = "",
    val lifespanYears: Int = 0,
    val waterRequirement: String = "",
    val plantingSeason: String = "",
    val environmentalImportance: String = "",
    val culturalSignificance: String = "",
    val descriptionEn: String = "",
    val descriptionKn: String = "",
    val isFavorite: Boolean = false
)

data class LeaderboardEntry(
    val userId: String = "",
    val userName: String = "",
    val photoUrl: String = "",
    val points: Int = 0,
    val treesTagged: Int = 0,
    val rank: Int = 0
)

data class EcoChallenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pointsReward: Int = 0,
    val endDate: Long = 0L,
    val isActive: Boolean = true
)

data class AiSpeciesResult(
    val speciesName: String = "",
    val speciesKn: String = "",
    val scientificName: String = "",
    val confidence: Float = 0f,
    val oxygenEstimate: Double = 0.0,
    val environmentalBenefits: String = "",
    val kannadaDescription: String = "",
    val healthSuggestions: String = ""
)
