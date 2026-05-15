package com.hasiru.usiru.mapper.domain.engine

import com.hasiru.usiru.mapper.domain.model.TreeMarker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OxygenScoreEngine @Inject constructor() {

    fun speciesFactor(species: String): Double {
        val key = species.trim().lowercase()
        return SPECIES_FACTORS.entries.firstOrNull { (name, _) ->
            key.contains(name)
        }?.value ?: DEFAULT_FACTOR
    }

    fun calculateOxygenScore(girthCm: Double, species: String): Double {
        if (girthCm <= 0) return 0.0
        return girthCm * speciesFactor(species)
    }

    fun recalculateTree(tree: TreeMarker): TreeMarker {
        val score = calculateOxygenScore(tree.girthCm, tree.species)
        return tree.copy(oxygenScore = score)
    }

    fun communityScore(trees: List<TreeMarker>): Double =
        trees.filter { it.status == com.hasiru.usiru.mapper.domain.model.ReportStatus.APPROVED }
            .sumOf { it.oxygenScore }

    fun areaScore(trees: List<TreeMarker>, centerLat: Double, centerLng: Double, radiusKm: Double): Double {
        return trees.filter { tree ->
            haversineKm(tree.latitude, tree.longitude, centerLat, centerLng) <= radiusKm
        }.sumOf { it.oxygenScore }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    companion object {
        const val DEFAULT_FACTOR = 1.0
        val SPECIES_FACTORS = mapOf(
            "neem" to 1.5,
            "peepal" to 1.8,
            "banyan" to 2.0,
            "coconut" to 1.2,
            "mango" to 1.4,
            "honge" to 1.6,
            "ashoka" to 1.3,
            "tamarind" to 1.5,
            "jackfruit" to 1.4
        )
    }
}
