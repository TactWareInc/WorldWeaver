package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Singleton service for handling lore entries across the application.
 * Manages world lore, history, myths, and other narrative elements.
 */
object LoreService {

    /**
     * Data class representing a lore entry
     */
    data class LoreEntry(
        val id: String = generateId(),
        val title: String,
        val content: String,
        val category: String, // e.g., "History", "Myth", "Religion", etc.
        val tags: List<String> = emptyList(),
        val relatedEntries: List<String> = emptyList(), // IDs of related lore entries
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now()
    )

    // In-memory storage for lore entries
    private val _loreEntries = mutableStateListOf<LoreEntry>()

    // Public read-only access to lore entries
    val loreEntries: SnapshotStateList<LoreEntry> = _loreEntries

    // Initialize with some sample lore entries
    init {
        addLoreEntry(
            "The Creation Myth",
            "In the beginning, there was only the void. From this emptiness emerged two primordial forces: Order and Chaos. Their eternal struggle gave birth to the material world and all its inhabitants.",
            "Mythology",
            listOf("creation", "gods", "origin")
        )
        
        addLoreEntry(
            "The Great War",
            "Five hundred years ago, the kingdoms of men united against the rising darkness from the north. Led by the legendary hero Aldric the Brave, they fought a decade-long war against the Lich King Malachar and his undead armies.",
            "History",
            listOf("war", "undead", "heroes")
        )
        
        addLoreEntry(
            "The Pantheon",
            "The world is watched over by twelve major deities, each representing different aspects of existence. The most widely worshipped are Solara (goddess of light), Noctis (god of darkness), Terran (god of earth), and Aquaria (goddess of water).",
            "Religion",
            listOf("gods", "worship", "divine")
        )
    }

    /**
     * Adds a new lore entry
     */
    fun addLoreEntry(title: String, content: String, category: String, tags: List<String> = emptyList(), relatedEntries: List<String> = emptyList()) {
        _loreEntries.add(
            LoreEntry(
                title = title,
                content = content,
                category = category,
                tags = tags,
                relatedEntries = relatedEntries
            )
        )
    }

    /**
     * Updates an existing lore entry
     */
    fun updateLoreEntry(id: String, title: String? = null, content: String? = null, category: String? = null, tags: List<String>? = null, relatedEntries: List<String>? = null) {
        val index = _loreEntries.indexOfFirst { it.id == id }
        if (index != -1) {
            val entry = _loreEntries[index]
            _loreEntries[index] = entry.copy(
                title = title ?: entry.title,
                content = content ?: entry.content,
                category = category ?: entry.category,
                tags = tags ?: entry.tags,
                relatedEntries = relatedEntries ?: entry.relatedEntries,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Removes a lore entry
     */
    fun removeLoreEntry(id: String) {
        _loreEntries.removeIf { it.id == id }
    }

    /**
     * Gets lore entries by category
     */
    fun getLoreEntriesByCategory(category: String): List<LoreEntry> {
        return _loreEntries.filter { it.category == category }
    }

    /**
     * Gets lore entries by tag
     */
    fun getLoreEntriesByTag(tag: String): List<LoreEntry> {
        return _loreEntries.filter { it.tags.contains(tag) }
    }

    /**
     * Searches lore entries by title or content
     */
    fun searchLoreEntries(query: String): List<LoreEntry> {
        val lowercaseQuery = query.lowercase()
        return _loreEntries.filter { 
            it.title.lowercase().contains(lowercaseQuery) || 
            it.content.lowercase().contains(lowercaseQuery) 
        }
    }

    /**
     * Generates a unique ID for a lore entry
     */
    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }
}