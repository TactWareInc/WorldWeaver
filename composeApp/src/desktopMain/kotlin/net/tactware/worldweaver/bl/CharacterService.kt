package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Singleton service for handling character information across the application.
 * Manages player characters, NPCs, monsters, and other entities.
 */
object CharacterService {

    /**
     * Enum representing character types
     */
    enum class CharacterType {
        PLAYER_CHARACTER,
        NON_PLAYER_CHARACTER,
        MONSTER
    }

    /**
     * Data class representing a character
     */
    data class Character(
        val id: String = generateId(),
        val name: String,
        val type: CharacterType,
        val race: String,
        val characterClass: String = "", // Only applicable for PCs and some NPCs
        val level: Int = 1, // Only applicable for PCs and some NPCs
        val attributes: Map<String, Int> = mapOf(
            "Strength" to 10,
            "Dexterity" to 10,
            "Constitution" to 10,
            "Intelligence" to 10,
            "Wisdom" to 10,
            "Charisma" to 10
        ),
        val hitPoints: Int = 10,
        val maxHitPoints: Int = 10,
        val armorClass: Int = 10,
        val background: String = "",
        val alignment: String = "",
        val description: String = "",
        val inventory: List<String> = emptyList(),
        val abilities: List<String> = emptyList(),
        val notes: String = "",
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now()
    )

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
            level = 5,
            attributes = mapOf(
                "Strength" to 16,
                "Dexterity" to 12,
                "Constitution" to 18,
                "Intelligence" to 10,
                "Wisdom" to 14,
                "Charisma" to 8
            ),
            hitPoints = 45,
            maxHitPoints = 45,
            armorClass = 18,
            background = "Soldier",
            alignment = "Lawful Good",
            description = "A stout dwarf with a braided beard and battle scars. Known for his unwavering loyalty and tactical prowess.",
            inventory = listOf("Plate Armor", "Warhammer", "Shield", "Potion of Healing (2)", "50 gold pieces"),
            abilities = listOf("Second Wind", "Action Surge", "Improved Critical", "Dwarven Resilience"),
            notes = "Member of the Ironheart clan from the mountains of Kragspire. Seeking vengeance against the orcs who destroyed his home."
        )

        // NPC
        addCharacter(
            name = "Elara Nightshade",
            type = CharacterType.NON_PLAYER_CHARACTER,
            race = "Elf",
            characterClass = "Wizard",
            level = 7,
            attributes = mapOf(
                "Strength" to 8,
                "Dexterity" to 14,
                "Constitution" to 12,
                "Intelligence" to 18,
                "Wisdom" to 16,
                "Charisma" to 14
            ),
            hitPoints = 35,
            maxHitPoints = 35,
            armorClass = 13,
            alignment = "Neutral Good",
            description = "A tall, slender elf with silver hair and piercing blue eyes. She speaks softly but with authority.",
            abilities = listOf("Arcane Recovery", "Spell Mastery", "Fey Ancestry"),
            notes = "Court mage to King Aldric. Has knowledge of ancient artifacts and can provide magical assistance to the party."
        )

        // Monster
        addCharacter(
            name = "Grommash the Devourer",
            type = CharacterType.MONSTER,
            race = "Troll",
            hitPoints = 84,
            maxHitPoints = 84,
            armorClass = 15,
            attributes = mapOf(
                "Strength" to 18,
                "Dexterity" to 13,
                "Constitution" to 20,
                "Intelligence" to 7,
                "Wisdom" to 9,
                "Charisma" to 7
            ),
            description = "A massive troll with green, warty skin and razor-sharp claws. Its regenerative abilities make it a formidable foe.",
            abilities = listOf("Regeneration", "Multiattack", "Keen Smell"),
            notes = "Lair located in the Darkwood Swamp. Weakness to fire damage."
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
        level: Int = 1,
        attributes: Map<String, Int> = mapOf(
            "Strength" to 10,
            "Dexterity" to 10,
            "Constitution" to 10,
            "Intelligence" to 10,
            "Wisdom" to 10,
            "Charisma" to 10
        ),
        hitPoints: Int = 10,
        maxHitPoints: Int = 10,
        armorClass: Int = 10,
        background: String = "",
        alignment: String = "",
        description: String = "",
        inventory: List<String> = emptyList(),
        abilities: List<String> = emptyList(),
        notes: String = ""
    ) {
        _characters.add(
            Character(
                name = name,
                type = type,
                race = race,
                characterClass = characterClass,
                level = level,
                attributes = attributes,
                hitPoints = hitPoints,
                maxHitPoints = maxHitPoints,
                armorClass = armorClass,
                background = background,
                alignment = alignment,
                description = description,
                inventory = inventory,
                abilities = abilities,
                notes = notes
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
        level: Int? = null,
        attributes: Map<String, Int>? = null,
        hitPoints: Int? = null,
        maxHitPoints: Int? = null,
        armorClass: Int? = null,
        background: String? = null,
        alignment: String? = null,
        description: String? = null,
        inventory: List<String>? = null,
        abilities: List<String>? = null,
        notes: String? = null
    ) {
        val index = _characters.indexOfFirst { it.id == id }
        if (index != -1) {
            val character = _characters[index]
            _characters[index] = character.copy(
                name = name ?: character.name,
                type = type ?: character.type,
                race = race ?: character.race,
                characterClass = characterClass ?: character.characterClass,
                level = level ?: character.level,
                attributes = attributes ?: character.attributes,
                hitPoints = hitPoints ?: character.hitPoints,
                maxHitPoints = maxHitPoints ?: character.maxHitPoints,
                armorClass = armorClass ?: character.armorClass,
                background = background ?: character.background,
                alignment = alignment ?: character.alignment,
                description = description ?: character.description,
                inventory = inventory ?: character.inventory,
                abilities = abilities ?: character.abilities,
                notes = notes ?: character.notes,
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

    /**
     * Generates a unique ID for a character
     */
    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }
}