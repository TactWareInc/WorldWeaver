package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single

/**
 * Singleton service for handling combat encounters across the application.
 * Manages combat encounters, initiative tracking, and combat statistics.
 */
@Single
class EncounterService {

    /**
     * Data class representing a combat encounter
     */
    data class Encounter(
        val id: String = Companion.generateId(),
        val name: String,
        val description: String,
        val location: String, // Location name or ID
        val difficulty: EncounterDifficulty = EncounterDifficulty.MEDIUM,
        val participants: List<EncounterParticipant> = emptyList(),
        val rewards: List<String> = emptyList(), // XP, treasure, etc.
        val notes: String = "",
        val isActive: Boolean = false,
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now()
    )

    /**
     * Data class representing a participant in an encounter
     */
    data class EncounterParticipant(
        val id: String = Companion.generateId(),
        val name: String,
        val type: ParticipantType,
        val initiative: Int = 0,
        val armorClass: Int = 10,
        val hitPoints: Int = 10,
        val maxHitPoints: Int = 10,
        val conditions: List<String> = emptyList(), // Conditions like "Poisoned", "Stunned", etc.
        val notes: String = ""
    )

    /**
     * Enum representing participant types
     */
    enum class ParticipantType {
        PLAYER_CHARACTER,
        ALLY,
        ENEMY
    }

    /**
     * Enum representing encounter difficulty
     */
    enum class EncounterDifficulty {
        EASY,
        MEDIUM,
        HARD,
        DEADLY
    }

    // In-memory storage for encounters
    private val _encounters = mutableStateListOf<Encounter>()

    // Public read-only access to encounters
    val encounters: SnapshotStateList<Encounter> = _encounters

    // Initialize with some sample encounters
    init {
        // A simple bandit ambush
        addEncounter(
            name = "Bandit Ambush",
            description = "A group of bandits ambushes the party on the road to Ravenhollow.",
            location = "Forest Road",
            difficulty = EncounterDifficulty.EASY,
            participants = listOf(
                EncounterParticipant(
                    name = "Thorne Ironheart",
                    type = ParticipantType.PLAYER_CHARACTER,
                    initiative = 15,
                    armorClass = 18,
                    hitPoints = 45,
                    maxHitPoints = 45
                ),
                EncounterParticipant(
                    name = "Bandit Leader",
                    type = ParticipantType.ENEMY,
                    initiative = 16,
                    armorClass = 15,
                    hitPoints = 32,
                    maxHitPoints = 32,
                    notes = "Dual-wields short swords, has Multiattack"
                ),
                EncounterParticipant(
                    name = "Bandit 1",
                    type = ParticipantType.ENEMY,
                    initiative = 12,
                    armorClass = 12,
                    hitPoints = 11,
                    maxHitPoints = 11
                ),
                EncounterParticipant(
                    name = "Bandit 2",
                    type = ParticipantType.ENEMY,
                    initiative = 8,
                    armorClass = 12,
                    hitPoints = 11,
                    maxHitPoints = 11
                )
            ),
            rewards = listOf("50 gold pieces", "Bandit leader's short swords", "100 XP per character")
        )

        // A more challenging undead encounter
        addEncounter(
            name = "Cemetery Guardians",
            description = "Undead rise from the graves as the party investigates the old cemetery of Ravenhollow.",
            location = "Old Cemetery",
            difficulty = EncounterDifficulty.HARD,
            participants = listOf(
                EncounterParticipant(
                    name = "Thorne Ironheart",
                    type = ParticipantType.PLAYER_CHARACTER,
                    initiative = 15,
                    armorClass = 18,
                    hitPoints = 45,
                    maxHitPoints = 45
                ),
                EncounterParticipant(
                    name = "Zombie Hulk",
                    type = ParticipantType.ENEMY,
                    initiative = 8,
                    armorClass = 14,
                    hitPoints = 85,
                    maxHitPoints = 85,
                    notes = "Resistant to non-magical weapons, vulnerable to radiant damage"
                ),
                EncounterParticipant(
                    name = "Skeleton Archer 1",
                    type = ParticipantType.ENEMY,
                    initiative = 13,
                    armorClass = 13,
                    hitPoints = 20,
                    maxHitPoints = 20,
                    notes = "Attacks from range with longbow"
                ),
                EncounterParticipant(
                    name = "Skeleton Archer 2",
                    type = ParticipantType.ENEMY,
                    initiative = 13,
                    armorClass = 13,
                    hitPoints = 20,
                    maxHitPoints = 20,
                    notes = "Attacks from range with longbow"
                ),
                EncounterParticipant(
                    name = "Skeleton Warrior 1",
                    type = ParticipantType.ENEMY,
                    initiative = 11,
                    armorClass = 15,
                    hitPoints = 26,
                    maxHitPoints = 26
                ),
                EncounterParticipant(
                    name = "Skeleton Warrior 2",
                    type = ParticipantType.ENEMY,
                    initiative = 11,
                    armorClass = 15,
                    hitPoints = 26,
                    maxHitPoints = 26
                )
            ),
            rewards = listOf("Ancient amulet (quest item)", "Potion of healing", "300 XP per character")
        )
    }

    /**
     * Adds a new encounter
     */
    fun addEncounter(
        name: String,
        description: String,
        location: String,
        difficulty: EncounterDifficulty = EncounterDifficulty.MEDIUM,
        participants: List<EncounterParticipant> = emptyList(),
        rewards: List<String> = emptyList(),
        notes: String = "",
        isActive: Boolean = false
    ): String {
        val id = Companion.generateId()
        _encounters.add(
            Encounter(
                id = id,
                name = name,
                description = description,
                location = location,
                difficulty = difficulty,
                participants = participants,
                rewards = rewards,
                notes = notes,
                isActive = isActive
            )
        )
        return id
    }

    /**
     * Updates an existing encounter
     */
    fun updateEncounter(
        id: String,
        name: String? = null,
        description: String? = null,
        location: String? = null,
        difficulty: EncounterDifficulty? = null,
        participants: List<EncounterParticipant>? = null,
        rewards: List<String>? = null,
        notes: String? = null,
        isActive: Boolean? = null
    ) {
        val index = _encounters.indexOfFirst { it.id == id }
        if (index != -1) {
            val encounter = _encounters[index]
            _encounters[index] = encounter.copy(
                name = name ?: encounter.name,
                description = description ?: encounter.description,
                location = location ?: encounter.location,
                difficulty = difficulty ?: encounter.difficulty,
                participants = participants ?: encounter.participants,
                rewards = rewards ?: encounter.rewards,
                notes = notes ?: encounter.notes,
                isActive = isActive ?: encounter.isActive,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Removes an encounter
     */
    fun removeEncounter(id: String) {
        _encounters.removeIf { it.id == id }
    }

    /**
     * Updates a participant in an encounter
     */
    fun updateParticipant(
        encounterId: String,
        participantId: String,
        name: String? = null,
        initiative: Int? = null,
        armorClass: Int? = null,
        hitPoints: Int? = null,
        maxHitPoints: Int? = null,
        conditions: List<String>? = null,
        notes: String? = null
    ) {
        val encounterIndex = _encounters.indexOfFirst { it.id == encounterId }
        if (encounterIndex != -1) {
            val encounter = _encounters[encounterIndex]
            val participantIndex = encounter.participants.indexOfFirst { it.id == participantId }

            if (participantIndex != -1) {
                val participant = encounter.participants[participantIndex]
                val updatedParticipants = encounter.participants.toMutableList()

                updatedParticipants[participantIndex] = participant.copy(
                    name = name ?: participant.name,
                    initiative = initiative ?: participant.initiative,
                    armorClass = armorClass ?: participant.armorClass,
                    hitPoints = hitPoints ?: participant.hitPoints,
                    maxHitPoints = maxHitPoints ?: participant.maxHitPoints,
                    conditions = conditions ?: participant.conditions,
                    notes = notes ?: participant.notes
                )

                _encounters[encounterIndex] = encounter.copy(
                    participants = updatedParticipants,
                    updatedAt = Clock.System.now()
                )
            }
        }
    }

    /**
     * Gets active encounters
     */
    fun getActiveEncounters(): List<Encounter> {
        return _encounters.filter { it.isActive }
    }

    /**
     * Sets an encounter as active
     */
    fun setEncounterActive(id: String, active: Boolean = true) {
        val index = _encounters.indexOfFirst { it.id == id }
        if (index != -1) {
            val encounter = _encounters[index]
            _encounters[index] = encounter.copy(
                isActive = active,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Searches encounters by name or description
     */
    fun searchEncounters(query: String): List<Encounter> {
        val lowercaseQuery = query.lowercase()
        return _encounters.filter { 
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) ||
            it.location.lowercase().contains(lowercaseQuery) ||
            it.notes.lowercase().contains(lowercaseQuery)
        }
    }

    companion object {
        /**
         * Generates a unique ID
         */
        internal fun generateId(): String {
            return System.currentTimeMillis().toString()
        }
    }
}
