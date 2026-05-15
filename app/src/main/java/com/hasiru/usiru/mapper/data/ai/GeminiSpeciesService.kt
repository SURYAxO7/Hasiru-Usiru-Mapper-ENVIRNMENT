package com.hasiru.usiru.mapper.data.ai

import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.hasiru.usiru.mapper.BuildConfig
import com.hasiru.usiru.mapper.domain.ai.SpeciesIdentificationService
import com.hasiru.usiru.mapper.domain.engine.OxygenScoreEngine
import com.hasiru.usiru.mapper.domain.model.AiSpeciesResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiSpeciesService @Inject constructor(
    private val oxygenScoreEngine: OxygenScoreEngine
) : SpeciesIdentificationService {

    private val model by lazy {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) null
        else GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    override suspend fun identifySpecies(imageBytes: ByteArray): Result<AiSpeciesResult> {
        return try {
            val gemini = model ?: return Result.success(fallbackIdentification())
            val prompt = """
                Identify the tree species in this image for urban India (Bengaluru/Mysuru).
                Respond ONLY in JSON with keys:
                speciesName, speciesKn, scientificName, confidence (0-1),
                environmentalBenefits, kannadaDescription, healthSuggestions.
                Focus on: Neem, Peepal, Honge, Banyan, Coconut, Mango, Ashoka, Tamarind.
            """.trimIndent()
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: return Result.success(fallbackIdentification())
            val response = gemini.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
            val text = response.text ?: return Result.success(fallbackIdentification())
            Result.success(parseAiJson(text))
        } catch (e: Exception) {
            Result.success(fallbackIdentification())
        }
    }

    override suspend fun recommendSpeciesForPit(
        soilType: String,
        waterAvailability: String,
        sunlightExposure: String,
        city: String
    ): List<String> {
        val recommendations = mutableListOf<String>()
        when {
            sunlightExposure.contains("FULL", ignoreCase = true) -> {
                recommendations += listOf("Neem", "Peepal", "Honge")
            }
            sunlightExposure.contains("SHADE", ignoreCase = true) -> {
                recommendations += listOf("Ashoka", "Mango")
            }
            else -> recommendations += listOf("Neem", "Mango", "Ashoka")
        }
        if (waterAvailability.contains("LOW", ignoreCase = true)) {
            recommendations.add(0, "Neem")
        }
        if (soilType.contains("CLAY", ignoreCase = true)) {
            recommendations += "Banyan"
        }
        if (city.contains("Mysuru", ignoreCase = true)) {
            recommendations += "Coconut"
        }
        return recommendations.distinct().take(5)
    }

    private fun parseAiJson(text: String): AiSpeciesResult {
        val jsonBlock = text.substringAfter("{", "").let { if (it.isEmpty()) text else "{$it" }
            .substringBeforeLast("}").let { if (it.contains("speciesName")) "$it}" else text }
        return try {
            val species = extractJsonValue(jsonBlock, "speciesName") ?: "Neem"
            val girth = 50.0
            AiSpeciesResult(
                speciesName = species,
                speciesKn = extractJsonValue(jsonBlock, "speciesKn") ?: "ಬೇವು",
                scientificName = extractJsonValue(jsonBlock, "scientificName") ?: "Azadirachta indica",
                confidence = extractJsonValue(jsonBlock, "confidence")?.toFloatOrNull() ?: 0.75f,
                oxygenEstimate = oxygenScoreEngine.calculateOxygenScore(girth, species),
                environmentalBenefits = extractJsonValue(jsonBlock, "environmentalBenefits")
                    ?: "Air purification and shade",
                kannadaDescription = extractJsonValue(jsonBlock, "kannadaDescription")
                    ?: "ಪರಿಸರ ಸ್ನೇಹಿ ಸ್ಥಳೀಯ ಮರ",
                healthSuggestions = extractJsonValue(jsonBlock, "healthSuggestions")
                    ?: "Monitor for pests; water during dry season"
            )
        } catch (_: Exception) {
            fallbackIdentification()
        }
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val regex = """"$key"\s*:\s*"([^"]*)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }

    private fun fallbackIdentification() = AiSpeciesResult(
        speciesName = "Neem",
        speciesKn = "ಬೇವು",
        scientificName = "Azadirachta indica",
        confidence = 0.6f,
        oxygenEstimate = oxygenScoreEngine.calculateOxygenScore(45.0, "Neem"),
        environmentalBenefits = "Natural air purifier, medicinal properties, drought resistant",
        kannadaDescription = "ಬೇವು ಮರವು ಔಷಧೀಯ ಗುಣಗಳನ್ನು ಹೊಂದಿದೆ ಮತ್ತು ವಾತಾವರಣವನ್ನು ಶುದ್ಧೀಕರಿಸುತ್ತದೆ",
        healthSuggestions = "Ensure adequate spacing; prune dead branches annually"
    )
}
