package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing spell slots for a specific level
 */
data class SpellSlots(
    val total: Int,
    val used: Int = 0
)