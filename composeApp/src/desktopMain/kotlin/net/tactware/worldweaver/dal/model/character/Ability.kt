package net.tactware.worldweaver.dal.model.character

/**
 * Enum representing ability scores in D&D 5E
 */
enum class Ability {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA;

    companion object {
        fun fromString(value: String): Ability {
            return when (value.uppercase()) {
                "STRENGTH", "STR" -> STRENGTH
                "DEXTERITY", "DEX" -> DEXTERITY
                "CONSTITUTION", "CON" -> CONSTITUTION
                "INTELLIGENCE", "INT" -> INTELLIGENCE
                "WISDOM", "WIS" -> WISDOM
                "CHARISMA", "CHA" -> CHARISMA
                else -> throw IllegalArgumentException("Unknown ability: $value")
            }
        }
    }
}