package net.tactware.worldweaver.dal.model.lore

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Data class representing a lore entry
 */
data class Lore(
    val id: String = generateId(),
    val campaignId: String = "",
    val title: String,
    val content: String,
    val category: String, // e.g., "History", "Myth", "Religion", etc.
    val tags: List<String> = emptyList(),
    val relatedEntries: List<String> = emptyList(), // IDs of related lore entries
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    companion object {
        /**
         * Generates a unique ID for a lore entry
         */
        internal fun generateId(): String {
            return System.currentTimeMillis().toString()
        }
    }
}