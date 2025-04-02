package net.tactware.worldweaver.dal.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Data class representing a campaign
 */
data class Campaign(
    val id: String = generateId(),
    val name: String,
    val description: String,
    val setting: String,
    val playerCharacters: List<String> = emptyList(), // Names or IDs of player characters
    val activeQuests: List<String> = emptyList(), // Names or IDs of active quests
    val completedQuests: List<String> = emptyList(), // Names or IDs of completed quests
    val notes: String = "",
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    companion object {
        /**
         * Generates a unique ID for a campaign
         */
        internal fun generateId(): String {
            return System.currentTimeMillis().toString()
        }
    }
}