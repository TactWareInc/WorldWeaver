package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single
import kotlin.math.floor

/**
 * Singleton service for handling character information across the application.
 * Manages player characters, NPCs, monsters, and other entities.
 */
@Single
class CharacterService {

    /**
     * Enum representing character types
     */
    enum class CharacterType {
        PLAYER_CHARACTER,
        NON_PLAYER_CHARACTER,
        MONSTER
    }

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

    /**
     * Enum representing skills in D&D 5E
     */
    enum class Skill(val ability: Ability) {
        ACROBATICS(Ability.DEXTERITY),
        ANIMAL_HANDLING(Ability.WISDOM),
        ARCANA(Ability.INTELLIGENCE),
        ATHLETICS(Ability.STRENGTH),
        DECEPTION(Ability.CHARISMA),
        HISTORY(Ability.INTELLIGENCE),
        INSIGHT(Ability.WISDOM),
        INTIMIDATION(Ability.CHARISMA),
        INVESTIGATION(Ability.INTELLIGENCE),
        MEDICINE(Ability.WISDOM),
        NATURE(Ability.INTELLIGENCE),
        PERCEPTION(Ability.WISDOM),
        PERFORMANCE(Ability.CHARISMA),
        PERSUASION(Ability.CHARISMA),
        RELIGION(Ability.INTELLIGENCE),
        SLEIGHT_OF_HAND(Ability.DEXTERITY),
        STEALTH(Ability.DEXTERITY),
        SURVIVAL(Ability.WISDOM)
    }

    /**
     * Enum representing character alignments in D&D 5E
     */
    enum class Alignment {
        LAWFUL_GOOD,
        NEUTRAL_GOOD,
        CHAOTIC_GOOD,
        LAWFUL_NEUTRAL,
        TRUE_NEUTRAL,
        CHAOTIC_NEUTRAL,
        LAWFUL_EVIL,
        NEUTRAL_EVIL,
        CHAOTIC_EVIL,
        UNALIGNED;

        companion object {
            fun fromString(value: String): Alignment {
                return when (value.uppercase().replace(" ", "_")) {
                    "LAWFUL_GOOD", "LG" -> LAWFUL_GOOD
                    "NEUTRAL_GOOD", "NG" -> NEUTRAL_GOOD
                    "CHAOTIC_GOOD", "CG" -> CHAOTIC_GOOD
                    "LAWFUL_NEUTRAL", "LN" -> LAWFUL_NEUTRAL
                    "TRUE_NEUTRAL", "NEUTRAL", "N" -> TRUE_NEUTRAL
                    "CHAOTIC_NEUTRAL", "CN" -> CHAOTIC_NEUTRAL
                    "LAWFUL_EVIL", "LE" -> LAWFUL_EVIL
                    "NEUTRAL_EVIL", "NE" -> NEUTRAL_EVIL
                    "CHAOTIC_EVIL", "CE" -> CHAOTIC_EVIL
                    "UNALIGNED" -> UNALIGNED
                    else -> throw IllegalArgumentException("Unknown alignment: $value")
                }
            }

            fun toString(alignment: Alignment): String {
                return when (alignment) {
                    LAWFUL_GOOD -> "Lawful Good"
                    NEUTRAL_GOOD -> "Neutral Good"
                    CHAOTIC_GOOD -> "Chaotic Good"
                    LAWFUL_NEUTRAL -> "Lawful Neutral"
                    TRUE_NEUTRAL -> "True Neutral"
                    CHAOTIC_NEUTRAL -> "Chaotic Neutral"
                    LAWFUL_EVIL -> "Lawful Evil"
                    NEUTRAL_EVIL -> "Neutral Evil"
                    CHAOTIC_EVIL -> "Chaotic Evil"
                    UNALIGNED -> "Unaligned"
                }
            }
        }
    }

    /**
     * Enum representing types of character relationships
     */
    enum class RelationshipType {
        PARENT,
        CHILD,
        SIBLING,
        SPOUSE,
        ANCESTOR,
        DESCENDANT,
        MENTOR,
        STUDENT,
        ALLY,
        RIVAL,
        ENEMY,
        OTHER;

        companion object {
            fun fromString(value: String): RelationshipType {
                return when (value.uppercase().replace(" ", "_")) {
                    "PARENT" -> PARENT
                    "CHILD" -> CHILD
                    "SIBLING" -> SIBLING
                    "SPOUSE" -> SPOUSE
                    "ANCESTOR" -> ANCESTOR
                    "DESCENDANT" -> DESCENDANT
                    "MENTOR" -> MENTOR
                    "STUDENT" -> STUDENT
                    "ALLY" -> ALLY
                    "RIVAL" -> RIVAL
                    "ENEMY" -> ENEMY
                    "OTHER" -> OTHER
                    else -> throw IllegalArgumentException("Unknown relationship type: $value")
                }
            }

            fun toString(relationshipType: RelationshipType): String {
                return when (relationshipType) {
                    PARENT -> "Parent"
                    CHILD -> "Child"
                    SIBLING -> "Sibling"
                    SPOUSE -> "Spouse"
                    ANCESTOR -> "Ancestor"
                    DESCENDANT -> "Descendant"
                    MENTOR -> "Mentor"
                    STUDENT -> "Student"
                    ALLY -> "Ally"
                    RIVAL -> "Rival"
                    ENEMY -> "Enemy"
                    OTHER -> "Other"
                }
            }
        }
    }

    /**
     * Data class representing a relationship between characters
     */
    data class CharacterRelationship(
        val relatedCharacterId: String,
        val relationshipType: RelationshipType,
        val description: String = ""
    )

    /**
     * Data class representing a proficiency in D&D 5E
     */
    data class Proficiency(
        val name: String,
        val type: ProficiencyType
    )

    /**
     * Enum representing types of proficiencies in D&D 5E
     */
    enum class ProficiencyType {
        ARMOR,
        WEAPON,
        TOOL,
        LANGUAGE,
        SAVING_THROW,
        SKILL
    }

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

    /**
     * Enum representing schools of magic in D&D 5E
     */
    enum class SpellSchool {
        ABJURATION,
        CONJURATION,
        DIVINATION,
        ENCHANTMENT,
        EVOCATION,
        ILLUSION,
        NECROMANCY,
        TRANSMUTATION
    }

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

    /**
     * Data class representing spell slots for a specific level
     */
    data class SpellSlots(
        val total: Int,
        val used: Int = 0
    )

    /**
     * Data class representing an item in D&D 5E
     */
    data class Item(
        val name: String,
        val quantity: Int = 1,
        val weight: Double = 0.0,
        val description: String = "",
        val type: ItemType = ItemType.MISCELLANEOUS,
        val equipped: Boolean = false,
        val properties: List<String> = emptyList()
    )

    /**
     * Enum representing types of items in D&D 5E
     */
    enum class ItemType {
        WEAPON,
        ARMOR,
        ADVENTURING_GEAR,
        TOOL,
        MOUNT,
        VEHICLE,
        TRADE_GOOD,
        TREASURE,
        MISCELLANEOUS
    }

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

    /**
     * Data class representing hit dice in D&D 5E
     */
    data class HitDice(
        val dieType: Int, // e.g., 6 for d6, 8 for d8, etc.
        val total: Int,
        val used: Int = 0
    )

    /**
     * Data class representing death saves in D&D 5E
     */
    data class DeathSaves(
        val successes: Int = 0,
        val failures: Int = 0
    )

    /**
     * Data class representing personality characteristics in D&D 5E
     */
    data class PersonalityCharacteristics(
        val personalityTraits: List<String> = emptyList(),
        val ideals: List<String> = emptyList(),
        val bonds: List<String> = emptyList(),
        val flaws: List<String> = emptyList()
    )

    /**
     * Data class representing a character
     */
    data class Character(
        val id: String = Companion.generateId(),
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
        val alignment: String = "", // Keeping string for backward compatibility
        val alignmentEnum: Alignment = Alignment.TRUE_NEUTRAL, // New enum version
        val personalityCharacteristics: PersonalityCharacteristics = PersonalityCharacteristics(),
        val lineage: String = "", // Character's family history or ancestry (kept for backward compatibility)
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
            val proficiencyBonus = Companion.calculateProficiencyBonus(level)
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
            val proficiencyBonus = Companion.calculateProficiencyBonus(level)
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
    }

    // In-memory storage for characters
    private val _characters = mutableStateListOf<Character>()

    // Public read-only access to characters
    val characters: SnapshotStateList<Character> = _characters

    // Initialize with some sample characters
    init {
        // Player Character
        addCharacter(
            name = "Thorne Ironheart",
            type = CharacterType.PLAYER_CHARACTER,
            race = "Dwarf",
            characterClass = "Fighter",
            subclass = "Champion",
            level = 5,
            experiencePoints = 6500,
            // Old format attributes
            attributes = mapOf(
                "Strength" to 16,
                "Dexterity" to 12,
                "Constitution" to 18,
                "Intelligence" to 10,
                "Wisdom" to 14,
                "Charisma" to 8
            ),
            // New format ability scores
            abilityScores = mapOf(
                Ability.STRENGTH to 16,
                Ability.DEXTERITY to 12,
                Ability.CONSTITUTION to 18,
                Ability.INTELLIGENCE to 10,
                Ability.WISDOM to 14,
                Ability.CHARISMA to 8
            ),
            hitPoints = 45,
            maxHitPoints = 45,
            temporaryHitPoints = 0,
            armorClass = 18,
            hitDice = HitDice(10, 5, 0), // d10 hit die for Fighter, 5 total (level 5), 0 used
            speed = 25, // Dwarf speed is 25 feet
            background = "Soldier",
            alignment = "Lawful Good",
            alignmentEnum = Alignment.LAWFUL_GOOD,
            personalityCharacteristics = PersonalityCharacteristics(
                personalityTraits = listOf("I'm always polite and respectful.", "I don't trust my gut feelings, so I tend to wait for others to act."),
                ideals = listOf("Responsibility. I do what I must and obey just authority. (Lawful)"),
                bonds = listOf("I fight for those who cannot fight for themselves."),
                flaws = listOf("I have a weakness for the vices of the city, especially fine food and drink.")
            ),
            description = "A stout dwarf with a braided beard and battle scars. Known for his unwavering loyalty and tactical prowess.",
            notes = "Member of the Ironheart clan from the mountains of Kragspire. Seeking vengeance against the orcs who destroyed his home.",
            inspiration = false,
            // Proficiencies
            savingThrowProficiencies = setOf(Ability.STRENGTH, Ability.CONSTITUTION),
            skillProficiencies = setOf(Skill.ATHLETICS, Skill.INTIMIDATION, Skill.PERCEPTION, Skill.SURVIVAL),
            otherProficiencies = listOf(
                Proficiency("All Armor", ProficiencyType.ARMOR),
                Proficiency("Shields", ProficiencyType.ARMOR),
                Proficiency("Simple Weapons", ProficiencyType.WEAPON),
                Proficiency("Martial Weapons", ProficiencyType.WEAPON),
                Proficiency("Smith's Tools", ProficiencyType.TOOL),
                Proficiency("Common", ProficiencyType.LANGUAGE),
                Proficiency("Dwarvish", ProficiencyType.LANGUAGE)
            ),
            // Old inventory format
            inventory = listOf("Plate Armor", "Warhammer", "Shield", "Potion of Healing (2)", "50 gold pieces"),
            // New inventory format
            items = listOf(
                Item("Plate Armor", 1, 65.0, "Heavy armor that provides excellent protection.", ItemType.ARMOR, true, listOf("AC 18", "Disadvantage on Stealth checks", "Strength 15 required")),
                Item("Warhammer", 1, 2.0, "A versatile weapon that can be wielded with one or two hands.", ItemType.WEAPON, true, listOf("1d8 bludgeoning (one-handed)", "1d10 bludgeoning (two-handed)", "Versatile")),
                Item("Shield", 1, 6.0, "A wooden or metal shield carried in one hand.", ItemType.ARMOR, true, listOf("+2 AC")),
                Item("Potion of Healing", 2, 0.5, "A magical potion that restores 2d4+2 hit points when consumed.", ItemType.ADVENTURING_GEAR, false, listOf("Healing")),
                Item("Backpack", 1, 5.0, "A leather backpack for carrying equipment.", ItemType.ADVENTURING_GEAR, true)
            ),
            currency = mapOf(
                "cp" to 0,
                "sp" to 0,
                "ep" to 0,
                "gp" to 50,
                "pp" to 0
            ),
            // Old abilities format
            abilities = listOf("Second Wind", "Action Surge", "Improved Critical", "Dwarven Resilience"),
            // New features format
            features = listOf(
                Feature("Second Wind", "Class: Fighter", "You can use a bonus action to regain hit points equal to 1d10 + your fighter level. Once you use this feature, you must finish a short or long rest before you can use it again.", 1, 1),
                Feature("Action Surge", "Class: Fighter", "You can push yourself beyond your normal limits for a moment. On your turn, you can take one additional action. Once you use this feature, you must finish a short or long rest before you can use it again.", 1, 1),
                Feature("Improved Critical", "Class: Fighter (Champion)", "Your weapon attacks score a critical hit on a roll of 19 or 20."),
                Feature("Dwarven Resilience", "Race: Dwarf", "You have advantage on saving throws against poison, and you have resistance against poison damage.")
            )
        )

        // NPC
        addCharacter(
            name = "Elara Nightshade",
            type = CharacterType.NON_PLAYER_CHARACTER,
            race = "Elf",
            characterClass = "Wizard",
            subclass = "School of Divination",
            level = 7,
            experiencePoints = 23000,
            // Old format attributes
            attributes = mapOf(
                "Strength" to 8,
                "Dexterity" to 14,
                "Constitution" to 12,
                "Intelligence" to 18,
                "Wisdom" to 16,
                "Charisma" to 14
            ),
            // New format ability scores
            abilityScores = mapOf(
                Ability.STRENGTH to 8,
                Ability.DEXTERITY to 14,
                Ability.CONSTITUTION to 12,
                Ability.INTELLIGENCE to 18,
                Ability.WISDOM to 16,
                Ability.CHARISMA to 14
            ),
            hitPoints = 35,
            maxHitPoints = 35,
            temporaryHitPoints = 0,
            armorClass = 13,
            hitDice = HitDice(6, 7, 0), // d6 hit die for Wizard, 7 total (level 7), 0 used
            speed = 30, // Elf speed is 30 feet
            background = "Sage",
            alignment = "Neutral Good",
            alignmentEnum = Alignment.NEUTRAL_GOOD,
            personalityCharacteristics = PersonalityCharacteristics(
                personalityTraits = listOf("I use polysyllabic words that convey the impression of great erudition.", "I've read every book in the world's greatest libraries."),
                ideals = listOf("Knowledge. The path to power and self-improvement is through knowledge. (Neutral)"),
                bonds = listOf("I work to preserve a library, university, or monastery."),
                flaws = listOf("I am easily distracted by the promise of information.")
            ),
            description = "A tall, slender elf with silver hair and piercing blue eyes. She speaks softly but with authority.",
            notes = "Court mage to King Aldric. Has knowledge of ancient artifacts and can provide magical assistance to the party.",
            inspiration = false,
            // Proficiencies
            savingThrowProficiencies = setOf(Ability.INTELLIGENCE, Ability.WISDOM),
            skillProficiencies = setOf(Skill.ARCANA, Skill.HISTORY, Skill.INVESTIGATION, Skill.PERCEPTION),
            otherProficiencies = listOf(
                Proficiency("Daggers", ProficiencyType.WEAPON),
                Proficiency("Darts", ProficiencyType.WEAPON),
                Proficiency("Slings", ProficiencyType.WEAPON),
                Proficiency("Quarterstaffs", ProficiencyType.WEAPON),
                Proficiency("Light Crossbows", ProficiencyType.WEAPON),
                Proficiency("Common", ProficiencyType.LANGUAGE),
                Proficiency("Elvish", ProficiencyType.LANGUAGE),
                Proficiency("Draconic", ProficiencyType.LANGUAGE),
                Proficiency("Dwarvish", ProficiencyType.LANGUAGE)
            ),
            // Old inventory format
            inventory = listOf("Spellbook", "Quarterstaff", "Component Pouch", "Scholar's Pack", "Arcane Focus (Crystal)"),
            // New inventory format
            items = listOf(
                Item("Spellbook", 1, 3.0, "A leather-bound book containing all of your spells.", ItemType.ADVENTURING_GEAR, true),
                Item("Quarterstaff", 1, 4.0, "A wooden staff that can be wielded with one or two hands.", ItemType.WEAPON, true, listOf("1d6 bludgeoning (one-handed)", "1d8 bludgeoning (two-handed)", "Versatile")),
                Item("Component Pouch", 1, 2.0, "A small, watertight leather belt pouch that has compartments to hold all the material components and other special items you need to cast your spells.", ItemType.ADVENTURING_GEAR, true),
                Item("Scholar's Pack", 1, 10.0, "Includes a backpack, a book of lore, a bottle of ink, an ink pen, 10 sheets of parchment, a little bag of sand, and a small knife.", ItemType.ADVENTURING_GEAR, true),
                Item("Arcane Focus (Crystal)", 1, 0.5, "A special item designed to channel the power of arcane spells.", ItemType.ADVENTURING_GEAR, true),
                Item("Robes", 1, 4.0, "Fine clothes befitting a court wizard.", ItemType.ADVENTURING_GEAR, true)
            ),
            currency = mapOf(
                "cp" to 0,
                "sp" to 0,
                "ep" to 0,
                "gp" to 75,
                "pp" to 5
            ),
            // Old abilities format
            abilities = listOf("Arcane Recovery", "Spell Mastery", "Fey Ancestry"),
            // New features format
            features = listOf(
                Feature("Arcane Recovery", "Class: Wizard", "You have learned to regain some of your magical energy by studying your spellbook. Once per day when you finish a short rest, you can choose expended spell slots to recover.", 1, 1),
                Feature("Portent", "Class: Wizard (Divination)", "When you finish a long rest, roll two d20s and record the numbers rolled. You can replace any attack roll, saving throw, or ability check made by you or a creature that you can see with one of these foretelling rolls.", 2, 2),
                Feature("Expert Divination", "Class: Wizard (Divination)", "When you cast a divination spell of 2nd level or higher using a spell slot, you regain one expended spell slot of a level lower than the spell you cast."),
                Feature("Fey Ancestry", "Race: Elf", "You have advantage on saving throws against being charmed, and magic can't put you to sleep."),
                Feature("Trance", "Race: Elf", "Elves don't need to sleep. Instead, they meditate deeply, remaining semiconscious, for 4 hours a day.")
            ),
            // Spellcasting
            spellcasting = Spellcasting(
                spellcastingAbility = Ability.INTELLIGENCE,
                spellcastingClass = "Wizard",
                spellSaveDC = 15, // 8 + proficiency bonus (3) + INT modifier (4)
                spellAttackBonus = 7, // proficiency bonus (3) + INT modifier (4)
                spellsKnown = listOf(
                    // Cantrips
                    Spell("Fire Bolt", 0, SpellSchool.EVOCATION, "1 action", "120 feet", "V, S", "Instantaneous", "You hurl a mote of fire at a creature or object within range. Make a ranged spell attack. On a hit, the target takes 2d10 fire damage."),
                    Spell("Mage Hand", 0, SpellSchool.CONJURATION, "1 action", "30 feet", "V, S", "1 minute", "A spectral, floating hand appears at a point you choose within range."),
                    Spell("Prestidigitation", 0, SpellSchool.TRANSMUTATION, "1 action", "10 feet", "V, S", "Up to 1 hour", "You create a minor magical effect."),
                    Spell("Ray of Frost", 0, SpellSchool.EVOCATION, "1 action", "60 feet", "V, S", "Instantaneous", "A frigid beam of blue-white light streaks toward a creature within range. Make a ranged spell attack. On a hit, it takes 2d8 cold damage, and its speed is reduced by 10 feet until the start of your next turn."),

                    // 1st level
                    Spell("Detect Magic", 1, SpellSchool.DIVINATION, "1 action", "Self", "V, S", "Concentration, up to 10 minutes", "You sense the presence of magic within 30 feet of you.", true),
                    Spell("Identify", 1, SpellSchool.DIVINATION, "1 minute", "Touch", "V, S, M", "Instantaneous", "You learn the properties of a magic item.", true),
                    Spell("Magic Missile", 1, SpellSchool.EVOCATION, "1 action", "120 feet", "V, S", "Instantaneous", "You create three glowing darts of magical force. Each dart hits a creature of your choice that you can see within range. A dart deals 1d4 + 1 force damage to its target.", true),
                    Spell("Shield", 1, SpellSchool.ABJURATION, "1 reaction", "Self", "V, S", "1 round", "An invisible barrier of magical force appears and protects you.", true),

                    // 2nd level
                    Spell("Detect Thoughts", 2, SpellSchool.DIVINATION, "1 action", "Self", "V, S, M", "Concentration, up to 1 minute", "You can read the thoughts of certain creatures.", true),
                    Spell("Misty Step", 2, SpellSchool.CONJURATION, "1 bonus action", "Self", "V", "Instantaneous", "Briefly surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space that you can see.", true),

                    // 3rd level
                    Spell("Clairvoyance", 3, SpellSchool.DIVINATION, "10 minutes", "1 mile", "V, S, M", "Concentration, up to 10 minutes", "You create an invisible sensor within range in a location familiar to you.", true),
                    Spell("Fireball", 3, SpellSchool.EVOCATION, "1 action", "150 feet", "V, S, M", "Instantaneous", "A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame.", true),

                    // 4th level
                    Spell("Arcane Eye", 4, SpellSchool.DIVINATION, "1 action", "30 feet", "V, S, M", "Concentration, up to 1 hour", "You create an invisible, magical eye within range that hovers in the air for the duration.", true)
                ),
                spellSlots = mapOf(
                    1 to SpellSlots(4, 0),
                    2 to SpellSlots(3, 0),
                    3 to SpellSlots(3, 0),
                    4 to SpellSlots(1, 0)
                )
            )
        )

        // Monster
        addCharacter(
            name = "Grommash the Devourer",
            type = CharacterType.MONSTER,
            race = "Troll",
            // Old format attributes
            attributes = mapOf(
                "Strength" to 18,
                "Dexterity" to 13,
                "Constitution" to 20,
                "Intelligence" to 7,
                "Wisdom" to 9,
                "Charisma" to 7
            ),
            // New format ability scores
            abilityScores = mapOf(
                Ability.STRENGTH to 18,
                Ability.DEXTERITY to 13,
                Ability.CONSTITUTION to 20,
                Ability.INTELLIGENCE to 7,
                Ability.WISDOM to 9,
                Ability.CHARISMA to 7
            ),
            hitPoints = 84,
            maxHitPoints = 84,
            temporaryHitPoints = 0,
            armorClass = 15,
            hitDice = HitDice(8, 8, 0), // d8 hit die, 8 total (CR 5 equivalent), 0 used
            speed = 30,
            alignment = "Chaotic Evil",
            alignmentEnum = Alignment.CHAOTIC_EVIL,
            description = "A massive troll with green, warty skin and razor-sharp claws. Its regenerative abilities make it a formidable foe.",
            notes = "Lair located in the Darkwood Swamp. Weakness to fire damage.",
            // Old abilities format
            abilities = listOf("Regeneration", "Multiattack", "Keen Smell"),
            // New features format
            features = listOf(
                Feature("Regeneration", "Race: Troll", "The troll regains 10 hit points at the start of its turn. If the troll takes acid or fire damage, this trait doesn't function at the start of the troll's next turn. The troll dies only if it starts its turn with 0 hit points and doesn't regenerate."),
                Feature("Keen Smell", "Race: Troll", "The troll has advantage on Wisdom (Perception) checks that rely on smell."),
                Feature("Multiattack", "Race: Troll", "The troll makes three attacks: one with its bite and two with its claws.")
            ),
            // Inventory items
            items = listOf(
                Item("Severed Head", 1, 5.0, "A grisly trophy from a previous victim.", ItemType.TREASURE),
                Item("Tattered Clothing", 1, 2.0, "Scraps of clothing that barely cover the troll's massive frame.", ItemType.ADVENTURING_GEAR, true),
                Item("Bone Necklace", 1, 1.0, "A necklace made from the bones of previous victims.", ItemType.TREASURE, true)
            ),
            currency = mapOf(
                "cp" to 150,
                "sp" to 75,
                "ep" to 0,
                "gp" to 25,
                "pp" to 0
            ),
            // Skills
            skillProficiencies = setOf(Skill.PERCEPTION)
        )
    }

    /**
     * Adds a new character
     */
    fun addCharacter(
        name: String,
        type: CharacterType,
        race: String,
        characterClass: String = "",
        subclass: String = "",
        level: Int = 1,
        experiencePoints: Int = 0,
        // Core attributes - old format
        attributes: Map<String, Int> = mapOf(
            "Strength" to 10,
            "Dexterity" to 10,
            "Constitution" to 10,
            "Intelligence" to 10,
            "Wisdom" to 10,
            "Charisma" to 10
        ),
        // New format for ability scores
        abilityScores: Map<Ability, Int> = mapOf(
            Ability.STRENGTH to 10,
            Ability.DEXTERITY to 10,
            Ability.CONSTITUTION to 10,
            Ability.INTELLIGENCE to 10,
            Ability.WISDOM to 10,
            Ability.CHARISMA to 10
        ),
        // Health and defense
        hitPoints: Int = 10,
        maxHitPoints: Int = 10,
        temporaryHitPoints: Int = 0,
        armorClass: Int = 10,
        hitDice: HitDice? = null,
        deathSaves: DeathSaves = DeathSaves(),
        // Movement
        speed: Int = 30,
        // Character details
        background: String = "",
        alignment: String = "",
        alignmentEnum: Alignment = Alignment.TRUE_NEUTRAL,
        personalityCharacteristics: PersonalityCharacteristics = PersonalityCharacteristics(),
        lineage: String = "",
        relationships: List<CharacterRelationship> = emptyList(),
        description: String = "",
        notes: String = "",
        inspiration: Boolean = false,
        // Proficiencies and skills
        savingThrowProficiencies: Set<Ability> = emptySet(),
        skillProficiencies: Set<Skill> = emptySet(),
        skillExpertise: Set<Skill> = emptySet(),
        otherProficiencies: List<Proficiency> = emptyList(),
        // Equipment and inventory - old format
        inventory: List<String> = emptyList(),
        // New format for inventory
        items: List<Item> = emptyList(),
        currency: Map<String, Int> = mapOf(
            "cp" to 0,
            "sp" to 0,
            "ep" to 0,
            "gp" to 0,
            "pp" to 0
        ),
        // Features and abilities - old format
        abilities: List<String> = emptyList(),
        // New format for features
        features: List<Feature> = emptyList(),
        // Spellcasting
        spellcasting: Spellcasting? = null
    ) {
        _characters.add(
            Character(
                name = name,
                type = type,
                race = race,
                characterClass = characterClass,
                subclass = subclass,
                level = level,
                experiencePoints = experiencePoints,
                attributes = attributes,
                abilityScores = abilityScores,
                hitPoints = hitPoints,
                maxHitPoints = maxHitPoints,
                temporaryHitPoints = temporaryHitPoints,
                armorClass = armorClass,
                hitDice = hitDice,
                deathSaves = deathSaves,
                speed = speed,
                background = background,
                alignment = alignment,
                alignmentEnum = alignmentEnum,
                personalityCharacteristics = personalityCharacteristics,
                lineage = lineage,
                relationships = relationships,
                description = description,
                notes = notes,
                inspiration = inspiration,
                savingThrowProficiencies = savingThrowProficiencies,
                skillProficiencies = skillProficiencies,
                skillExpertise = skillExpertise,
                otherProficiencies = otherProficiencies,
                inventory = inventory,
                items = items,
                currency = currency,
                abilities = abilities,
                features = features,
                spellcasting = spellcasting
            )
        )
    }

    /**
     * Updates an existing character
     */
    fun updateCharacter(
        id: String,
        name: String? = null,
        type: CharacterType? = null,
        race: String? = null,
        characterClass: String? = null,
        subclass: String? = null,
        level: Int? = null,
        experiencePoints: Int? = null,
        // Core attributes - old format
        attributes: Map<String, Int>? = null,
        // New format for ability scores
        abilityScores: Map<Ability, Int>? = null,
        // Health and defense
        hitPoints: Int? = null,
        maxHitPoints: Int? = null,
        temporaryHitPoints: Int? = null,
        armorClass: Int? = null,
        hitDice: HitDice? = null,
        deathSaves: DeathSaves? = null,
        // Movement
        speed: Int? = null,
        // Character details
        background: String? = null,
        alignment: String? = null,
        alignmentEnum: Alignment? = null,
        personalityCharacteristics: PersonalityCharacteristics? = null,
        lineage: String? = null,
        relationships: List<CharacterRelationship>? = null,
        description: String? = null,
        notes: String? = null,
        inspiration: Boolean? = null,
        // Proficiencies and skills
        savingThrowProficiencies: Set<Ability>? = null,
        skillProficiencies: Set<Skill>? = null,
        skillExpertise: Set<Skill>? = null,
        otherProficiencies: List<Proficiency>? = null,
        // Equipment and inventory - old format
        inventory: List<String>? = null,
        // New format for inventory
        items: List<Item>? = null,
        currency: Map<String, Int>? = null,
        // Features and abilities - old format
        abilities: List<String>? = null,
        // New format for features
        features: List<Feature>? = null,
        // Spellcasting
        spellcasting: Spellcasting? = null
    ) {
        val index = _characters.indexOfFirst { it.id == id }
        if (index != -1) {
            val character = _characters[index]
            _characters[index] = character.copy(
                name = name ?: character.name,
                type = type ?: character.type,
                race = race ?: character.race,
                characterClass = characterClass ?: character.characterClass,
                subclass = subclass ?: character.subclass,
                level = level ?: character.level,
                experiencePoints = experiencePoints ?: character.experiencePoints,
                attributes = attributes ?: character.attributes,
                abilityScores = abilityScores ?: character.abilityScores,
                hitPoints = hitPoints ?: character.hitPoints,
                maxHitPoints = maxHitPoints ?: character.maxHitPoints,
                temporaryHitPoints = temporaryHitPoints ?: character.temporaryHitPoints,
                armorClass = armorClass ?: character.armorClass,
                hitDice = hitDice ?: character.hitDice,
                deathSaves = deathSaves ?: character.deathSaves,
                speed = speed ?: character.speed,
                background = background ?: character.background,
                alignment = alignment ?: character.alignment,
                alignmentEnum = alignmentEnum ?: character.alignmentEnum,
                personalityCharacteristics = personalityCharacteristics ?: character.personalityCharacteristics,
                lineage = lineage ?: character.lineage,
                relationships = relationships ?: character.relationships,
                description = description ?: character.description,
                notes = notes ?: character.notes,
                inspiration = inspiration ?: character.inspiration,
                savingThrowProficiencies = savingThrowProficiencies ?: character.savingThrowProficiencies,
                skillProficiencies = skillProficiencies ?: character.skillProficiencies,
                skillExpertise = skillExpertise ?: character.skillExpertise,
                otherProficiencies = otherProficiencies ?: character.otherProficiencies,
                inventory = inventory ?: character.inventory,
                items = items ?: character.items,
                currency = currency ?: character.currency,
                abilities = abilities ?: character.abilities,
                features = features ?: character.features,
                spellcasting = spellcasting ?: character.spellcasting,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Removes a character
     */
    fun removeCharacter(id: String) {
        _characters.removeIf { it.id == id }
    }

    /**
     * Gets characters by type
     */
    fun getCharactersByType(type: CharacterType): List<Character> {
        return _characters.filter { it.type == type }
    }

    /**
     * Gets player characters
     */
    fun getPlayerCharacters(): List<Character> {
        return getCharactersByType(CharacterType.PLAYER_CHARACTER)
    }

    /**
     * Gets non-player characters
     */
    fun getNonPlayerCharacters(): List<Character> {
        return getCharactersByType(CharacterType.NON_PLAYER_CHARACTER)
    }

    /**
     * Gets monsters
     */
    fun getMonsters(): List<Character> {
        return getCharactersByType(CharacterType.MONSTER)
    }

    /**
     * Searches characters by name or description
     */
    fun searchCharacters(query: String): List<Character> {
        val lowercaseQuery = query.lowercase()
        return _characters.filter { 
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) ||
            it.notes.lowercase().contains(lowercaseQuery)
        }
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
