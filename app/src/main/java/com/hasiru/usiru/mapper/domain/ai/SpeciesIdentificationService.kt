package com.hasiru.usiru.mapper.domain.ai

import com.hasiru.usiru.mapper.domain.model.AiSpeciesResult

interface SpeciesIdentificationService {
    suspend fun identifySpecies(imageBytes: ByteArray): Result<AiSpeciesResult>
    suspend fun recommendSpeciesForPit(
        soilType: String,
        waterAvailability: String,
        sunlightExposure: String,
        city: String
    ): List<String>
}
