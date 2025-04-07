package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing hit dice in D&D 5E
 */
data class HitDice(
    val dieType: Int, // e.g., 6 for d6, 8 for d8, etc.
    val total: Int,
    val used: Int = 0
)