package net.tactware.worldweaver.dal.repository

import kotlinx.datetime.Instant
import net.tactware.worldweaver.dal.model.lore.Lore

/**
 * Repository interface for Lore data access.
 * Defines methods for CRUD operations and search functionality.
 */
interface LoreRepository {
    /**
     * Get all lore entries ordered by updated date (descending)
     */
    fun getAllLoreEntries(): List<Lore>

    /**
     * Get a lore entry by ID
     */
    fun getLoreEntryById(id: String): Lore?

    /**
     * Insert a new lore entry
     */
    fun insertLoreEntry(
        id: String,
        campaignId: String,
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        relatedEntries: List<String>,
        createdAt: Instant,
        updatedAt: Instant
    )

    /**
     * Update an existing lore entry
     */
    fun updateLoreEntry(
        id: String,
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        relatedEntries: List<String>,
        updatedAt: Instant
    )

    /**
     * Delete a lore entry by ID
     */
    fun deleteLoreEntry(id: String)

    /**
     * Search lore entries using Full-Text Search
     */
    fun searchLoreEntries(query: String): List<Lore>

    /**
     * Search lore entries using LIKE (fallback)
     */
    fun searchLoreEntriesLike(query: String): List<Lore>

    /**
     * Get lore entries by category
     */
    fun getLoreEntriesByCategory(category: String): List<Lore>

    /**
     * Get lore entries by tag
     */
    fun getLoreEntriesByTag(tag: String): List<Lore>
}
