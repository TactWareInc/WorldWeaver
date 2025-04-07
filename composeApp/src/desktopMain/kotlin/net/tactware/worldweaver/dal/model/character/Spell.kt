package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing a spell in D&D 5E
 */
data class Spell(
    val name: String,
    val level: Int, // 0 for cantrips, 1-9 for leveled spells
    val school: SpellSchool,
    val castingTime: String,
    val range: String,
    val components: String,
    val duration: String,
    val description: String,
    val prepared: Boolean = false
)