package net.tactware.worldweaver.dal.model.character

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.floor

/**
 * Data class representing a character
 */
data class Character(
    val id: String = generateId(),
    val name: String,
    val type: CharacterType,
    val race: String,
    val characterClass: String = "", // Only applicable for PCs and some NPCs
    val subclass: String = "", // Only applicable for PCs and some NPCs
    val level: Int = 1, // Only applicable for PCs and some NPCs
    val experiencePoints: Int = 0, // Only applicable for PCs

    // Core attributes - keeping the old format for backward compatibility
    val attributes: Map<String, Int> = mapOf(
        "Strength" to 10,
        "Dexterity" to 10,
        "Constitution" to 10,
        "Intelligence" to 10,
        "Wisdom" to 10,
        "Charisma" to 10
    ),

    // New format for ability scores
    val abilityScores: Map<Ability, Int> = mapOf(
        Ability.STRENGTH to 10,
        Ability.DEXTERITY to 10,
        Ability.CONSTITUTION to 10,
        Ability.INTELLIGENCE to 10,
        Ability.WISDOM to 10,
        Ability.CHARISMA to 10
    ),

    // Health and defense
    val hitPoints: Int = 10,
    val maxHitPoints: Int = 10,
    val temporaryHitPoints: Int = 0,
    val armorClass: Int = 10,
    val hitDice: HitDice? = null,
    val deathSaves: DeathSaves = DeathSaves(),

    // Movement
    val speed: Int = 30, // Base walking speed in feet

    // Character details
    val background: String = "",
    val alignment: Alignment = Alignment.TRUE_NEUTRAL, // New enum version
    val personalityCharacteristics: PersonalityCharacteristics = PersonalityCharacteristics(),
    val relationships: List<CharacterRelationship> = emptyList(), // Character's relationships with other characters
    val description: String = "",
    val notes: String = "",
    val inspiration: Boolean = false,

    // Proficiencies and skills
    val savingThrowProficiencies: Set<Ability> = emptySet(),
    val skillProficiencies: Set<Skill> = emptySet(),
    val skillExpertise: Set<Skill> = emptySet(), // For double proficiency
    val otherProficiencies: List<Proficiency> = emptyList(),

    // Equipment and inventory - keeping old format for backward compatibility
    val inventory: List<String> = emptyList(),
    // New format for inventory
    val items: List<Item> = emptyList(),
    val currency: Map<String, Int> = mapOf(
        "cp" to 0, // Copper pieces
        "sp" to 0, // Silver pieces
        "ep" to 0, // Electrum pieces
        "gp" to 0, // Gold pieces
        "pp" to 0  // Platinum pieces
    ),

    // Features, traits, and abilities - keeping old format for backward compatibility
    val abilities: List<String> = emptyList(),
    // New format for features
    val features: List<Feature> = emptyList(),

    // Spellcasting (if applicable)
    val spellcasting: Spellcasting? = null,

    // Metadata
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    /**
     * Calculates the ability modifier for a given ability score
     */
    fun getAbilityModifier(ability: Ability): Int {
        val score = abilityScores[ability] ?: 10
        return floor((score - 10) / 2.0).toInt()
    }

    /**
     * Calculates the saving throw bonus for a given ability
     */
    fun getSavingThrowBonus(ability: Ability): Int {
        val modifier = getAbilityModifier(ability)
        val proficiencyBonus = calculateProficiencyBonus(level)
        return if (ability in savingThrowProficiencies) {
            modifier + proficiencyBonus
        } else {
            modifier
        }
    }

    /**
     * Calculates the skill bonus for a given skill
     */
    fun getSkillBonus(skill: Skill): Int {
        val modifier = getAbilityModifier(skill.ability)
        val proficiencyBonus = calculateProficiencyBonus(level)
        return when {
            skill in skillExpertise -> modifier + (proficiencyBonus * 2)
            skill in skillProficiencies -> modifier + proficiencyBonus
            else -> modifier
        }
    }

    /**
     * Calculates the passive perception score
     */
    fun getPassivePerception(): Int {
        return 10 + getSkillBonus(Skill.PERCEPTION)
    }

    /**
     * Calculates the initiative bonus (typically just DEX modifier)
     */
    fun getInitiativeBonus(): Int {
        return getAbilityModifier(Ability.DEXTERITY)
    }


    companion object {
        /**
         * Generates a unique ID for a character
         */
        internal fun generateId(): String {
            return System.currentTimeMillis().toString()
        }

        /**
         * Calculates the proficiency bonus based on character level
         */
        internal fun calculateProficiencyBonus(level: Int): Int {
            return when {
                level < 5 -> 2
                level < 9 -> 3
                level < 13 -> 4
                level < 17 -> 5
                else -> 6
            }
        }
    }
}