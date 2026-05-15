package com.hasiru.usiru.mapper.data.repository

import com.hasiru.usiru.mapper.data.local.HasiruDatabase
import com.hasiru.usiru.mapper.data.mapper.toDomain
import com.hasiru.usiru.mapper.data.mapper.toEntity
import com.hasiru.usiru.mapper.domain.model.SpeciesInfo
import com.hasiru.usiru.mapper.domain.repository.SpeciesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesRepositoryImpl @Inject constructor(
    private val database: HasiruDatabase
) : SpeciesRepository {

    override fun observeSpecies(): Flow<List<SpeciesInfo>> =
        database.speciesDao().observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun searchSpecies(query: String): List<SpeciesInfo> =
        database.speciesDao().search(query).map { it.toDomain() }

    override suspend fun toggleFavorite(id: String): Result<Unit> = try {
        val current = database.speciesDao().search("").find { it.id == id }
        val fav = !(current?.isFavorite ?: false)
        database.speciesDao().setFavorite(id, fav)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun seedDefaultSpecies() {
        val existing = database.speciesDao().search("")
        if (existing.isNotEmpty()) return
        database.speciesDao().insertAll(DEFAULT_SPECIES.map { it.toEntity() })
    }

    companion object {
        val DEFAULT_SPECIES = listOf(
            SpeciesInfo(
                id = "neem", nameEn = "Neem", nameKn = "ಬೇವು",
                scientificName = "Azadirachta indica", oxygenFactor = 1.5,
                medicinalUses = "Antibacterial, antifungal, skin treatments",
                oxygenBenefits = "High oxygen production, air purification",
                lifespanYears = 200, waterRequirement = "Low to moderate",
                plantingSeason = "Monsoon (June-September)",
                environmentalImportance = "Drought resistant, urban heat reduction",
                culturalSignificance = "Sacred in Indian tradition",
                descriptionEn = "Native Indian tree known for medicinal properties",
                descriptionKn = "ಔಷಧೀಯ ಗುಣಗಳಿಗೆ ಪ್ರಸಿದ್ಧ ಸ್ಥಳೀಯ ಮರ"
            ),
            SpeciesInfo(
                id = "peepal", nameEn = "Peepal", nameKn = "ಅರಳಿ ಮರ",
                scientificName = "Ficus religiosa", oxygenFactor = 1.8,
                medicinalUses = "Asthma, digestive disorders",
                oxygenBenefits = "Releases oxygen even at night",
                lifespanYears = 900, waterRequirement = "Moderate",
                plantingSeason = "Rainy season",
                environmentalImportance = "Excellent carbon sequestration",
                culturalSignificance = "Sacred tree in Hinduism and Buddhism",
                descriptionEn = "Sacred fig tree with exceptional oxygen output",
                descriptionKn = "ರಾತ್ರಿಯಲ್ಲೂ ಆಮ್ಲಜನಕ ಬಿಡುಗಡೆ ಮಾಡುವ ಪವಿತ್ರ ಮರ"
            ),
            SpeciesInfo(
                id = "honge", nameEn = "Honge (Indian Beech)", nameKn = "ಹೊಂಗೆ",
                scientificName = "Pongamia pinnata", oxygenFactor = 1.6,
                medicinalUses = "Skin diseases, rheumatism",
                oxygenBenefits = "Nitrogen fixing, improves soil",
                lifespanYears = 100, waterRequirement = "Low",
                plantingSeason = "June-July",
                environmentalImportance = "Ideal for roadside and urban planting",
                culturalSignificance = "Traditional Karnataka native species",
                descriptionEn = "Hardy native tree ideal for Bengaluru roadsides",
                descriptionKn = "ಬೆಂಗಳೂರು ರಸ್ತೆಬದಿಗೆ ಸೂಕ್ತವಾದ ಸ್ಥಳೀಯ ಮರ"
            ),
            SpeciesInfo(
                id = "banyan", nameEn = "Banyan", nameKn = "ಆಲದ ಮರ",
                scientificName = "Ficus benghalensis", oxygenFactor = 2.0,
                medicinalUses = "Diabetes, diarrhea treatment",
                oxygenBenefits = "Largest canopy, maximum shade and oxygen",
                lifespanYears = 1000, waterRequirement = "Moderate to high",
                plantingSeason = "Monsoon",
                environmentalImportance = "Supports entire ecosystems",
                culturalSignificance = "National tree of India",
                descriptionEn = "Giant shade tree with highest oxygen factor",
                descriptionKn = "ಅತ್ಯಧಿಕ ಆಮ್ಲಜನಕ ಉತ್ಪಾದನೆಯ ದೈತ್ಯ ಮರ"
            ),
            SpeciesInfo(
                id = "coconut", nameEn = "Coconut", nameKn = "ತೆಂಗಿನ ಮರ",
                scientificName = "Cocos nucifera", oxygenFactor = 1.2,
                medicinalUses = "Hydration, heart health",
                oxygenBenefits = "Coastal climate adaptation",
                lifespanYears = 80, waterRequirement = "High",
                plantingSeason = "May-June",
                environmentalImportance = "Coastal and Mysuru region suitable",
                culturalSignificance = "Integral to South Indian culture",
                descriptionEn = "Versatile palm for coastal and warm regions",
                descriptionKn = "ತೀರ ಮತ್ತು ಬೆಚ್ಚಗಿನ ಪ್ರದೇಶಗಳಿಗೆ ಸೂಕ್ತ"
            ),
            SpeciesInfo(
                id = "mango", nameEn = "Mango", nameKn = "ಮಾವು",
                scientificName = "Mangifera indica", oxygenFactor = 1.4,
                medicinalUses = "Vitamins A and C, digestive aid",
                oxygenBenefits = "Fruit production with environmental benefits",
                lifespanYears = 300, waterRequirement = "Moderate",
                plantingSeason = "July-August",
                environmentalImportance = "Food security and green cover",
                culturalSignificance = "National fruit of India",
                descriptionEn = "King of fruits with strong environmental value",
                descriptionKn = "ಪರಿಸರ ಮತ್ತು ಆಹಾರ ಸುರಕ್ಷೆಗೆ ಮುಖ್ಯ ಮರ"
            ),
            SpeciesInfo(
                id = "ashoka", nameEn = "Ashoka", nameKn = "ಅಶೋಕ",
                scientificName = "Polyalthia longifolia", oxygenFactor = 1.3,
                medicinalUses = "Skin conditions, pain relief",
                oxygenBenefits = "Narrow crown ideal for urban streets",
                lifespanYears = 50, waterRequirement = "Moderate",
                plantingSeason = "June-September",
                environmentalImportance = "Perfect for narrow urban spaces",
                culturalSignificance = "Symbol of peace and beauty",
                descriptionEn = "Columnar tree perfect for urban street planting",
                descriptionKn = "ನಗರ ಬೀದಿಗಳಿಗೆ ಸೂಕ್ತವಾದ ಸ್ತಂಭಾಕಾರ ಮರ"
            )
        )
    }
}
