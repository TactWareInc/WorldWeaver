package net.tactware.worldweaver.dal.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.model.character.Ability
import net.tactware.worldweaver.dal.model.character.Alignment
import net.tactware.worldweaver.dal.model.character.CharacterType
import net.tactware.worldweaver.dal.model.character.Feature
import net.tactware.worldweaver.dal.model.character.Item

/**
 * Repository interface for Character data access.
 * Defines methods for CRUD operations and search functionality.
 */
interface CharacterRepository {
    /**
     * Get all characters ordered by updated date (descending)
     */
    fun getAllCharacters(): List<Character>

    /**
     * Get a flow of all characters ordered by updated date (descending)
     */
    fun getCharactersFlow(): Flow<List<Character>>

    /**
     * Get a character by ID as a Flow
     */
    fun getCharacterById(id: String): Character?

    /**
     * Insert a new character
     */
    fun insertCharacter(
        id: String,
        campaignId: String,
        name: String,
        type: CharacterType,
        race: String,
        characterClass: String,
        subclass: String,
        level: Int,
        experiencePoints: Int,
        abilityScores: Map<Ability, Int>,
        hitPoints: Int,
        maxHitPoints: Int,
        temporaryHitPoints: Int,
        armorClass: Int,
        background: String,
        alignment: Alignment,
        description: String,
        notes: String,
        items: List<Item>,
        currency: Map<String, Int>,
        features: List<Feature>,
        createdAt: Instant,
        updatedAt: Instant
    )

    /**
     * Update an existing character
     */
    fun updateCharacter(
        id: String,
        name: String,
        type: CharacterType,
        race: String,
        characterClass: String,
        subclass: String,
        level: Int,
        experiencePoints: Int,
        abilityScores: Map<Ability, Int>,
        hitPoints: Int,
        maxHitPoints: Int,
        temporaryHitPoints: Int,
        armorClass: Int,
        background: String,
        alignment: Alignment,
        description: String,
        notes: String,
        items: List<Item>,
        currency: Map<String, Int>,
        features: List<Feature>,
        updatedAt: Instant
    )

    /**
     * Delete a character by ID
     */
    fun deleteCharacter(id: String)

    /**
     * Search characters using Full-Text Search
     */
    fun searchCharacters(query: String): List<Character>

    /**
     * Search characters using LIKE (fallback)
     */
    fun searchCharactersLike(query: String): List<Character>

    /**
     * Get characters by type
     */
    fun getCharactersByType(type: CharacterType): List<Character>
}
