package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing spellcasting ability in D&D 5E
 */
data class Spellcasting(
    val spellcastingAbility: Ability,
    val spellcastingClass: String,
    val spellSaveDC: Int,
    val spellAttackBonus: Int,
    val spellsKnown: List<Spell> = emptyList(),
    val spellSlots: Map<Int, SpellSlots> = emptyMap() // Key is spell level, value is slots
)