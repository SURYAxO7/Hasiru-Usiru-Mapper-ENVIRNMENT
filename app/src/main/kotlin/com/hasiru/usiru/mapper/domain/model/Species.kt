package com.hasiru.usiru.mapper.domain.model

/**
 * Represents a tree species with oxygen scoring factors
 */
data class Species(
    val id: String,
    val name: String,
    val scientificName: String,
    val oxygenFactor: Double,
    val description: String,
    val kannada: String,
    val isNative: Boolean
)

object SpeciesRegistry {
    val species = mapOf(
        "neem" to Species(
            id = "neem",
            name = "Neem",
            scientificName = "Azadirachta indica",
            oxygenFactor = 1.5,
            description = "Fast-growing tree with medicinal properties",
            kannada = "ವೃಕ್ಷ ನೀಮ್",
            isNative = true
        ),
        "peepal" to Species(
            id = "peepal",
            name = "Peepal",
            scientificName = "Ficus religiosa",
            oxygenFactor = 1.8,
            description = "Sacred fig tree, excellent oxygen producer",
            kannada = "ಅರಲಿ",
            isNative = true
        ),
        "banyan" to Species(
            id = "banyan",
            name = "Banyan",
            scientificName = "Ficus benghalensis",
            oxygenFactor = 2.0,
            description = "Largest canopy coverage tree",
            kannada = "ಮರೆ",
            isNative = true
        ),
        "coconut" to Species(
            id = "coconut",
            name = "Coconut",
            scientificName = "Cocos nucifera",
            oxygenFactor = 1.2,
            description = "Tropical palm with multiple benefits",
            kannada = "ತೆಂಗಿನ ಮರ",
            isNative = false
        ),
        "mango" to Species(
            id = "mango",
            name = "Mango",
            scientificName = "Mangifera indica",
            oxygenFactor = 1.4,
            description = "Fruit tree with good shade",
            kannada = "ಮಾವಿನ ಮರ",
            isNative = true
        ),
        "honge" to Species(
            id = "honge",
            name = "Honge",
            scientificName = "Millettia pinnata",
            oxygenFactor = 1.6,
            description = "Oil-bearing tree, nitrogen fixer",
            kannada = "ಹೊಂಗೆ ಮರ",
            isNative = true
        ),
        "ashoka" to Species(
            id = "ashoka",
            name = "Ashoka",
            scientificName = "Saraca asoca",
            oxygenFactor = 1.3,
            description = "Flowering tree, ornamental value",
            kannada = "ಅಶೋಕ ಮರ",
            isNative = true
        )
    )
}
