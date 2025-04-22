package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing a proficiency in D&D 5E
 */
data class Proficiency(
    val name: String,
    val type: ProficiencyType
)