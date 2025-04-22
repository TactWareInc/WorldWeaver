package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing a feature or trait in D&D 5E
 */
data class Feature(
    val name: String,
    val source: String, // e.g., "Race: Dwarf", "Class: Fighter", "Background: Soldier"
    val description: String,
    val usesPerDay: Int = 0,
    val usesRemaining: Int = 0
)