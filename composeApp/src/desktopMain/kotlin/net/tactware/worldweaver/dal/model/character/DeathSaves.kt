package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing death saves in D&D 5E
 */
data class DeathSaves(
    val successes: Int = 0,
    val failures: Int = 0
)