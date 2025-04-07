package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing personality characteristics in D&D 5E
 */
data class PersonalityCharacteristics(
    val personalityTraits: List<String> = emptyList(),
    val ideals: List<String> = emptyList(),
    val bonds: List<String> = emptyList(),
    val flaws: List<String> = emptyList()
)