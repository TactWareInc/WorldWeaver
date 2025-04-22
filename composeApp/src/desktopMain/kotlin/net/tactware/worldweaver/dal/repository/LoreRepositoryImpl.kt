package net.tactware.worldweaver.dal.repository

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import net.tactware.worldweaver.dal.model.lore.Lore
import net.tactware.worldweaver.dal.db.DatabaseProvider
import org.koin.core.annotation.Single
import migrations.net.tactware.worldweaver.Lore as LoreDb


/**
 * Implementation of the LoreRepository interface.
 * Handles database operations for Lore entities.
 */
@Single(binds = [LoreRepository::class])
class LoreRepositoryImpl(
    private val databaseProvider: DatabaseProvider
) : LoreRepository {

    private val mapper : (LoreDb) -> Lore = {
        Lore(
            id = it.id,
            campaignId = it.campaignId,
            title = it.title,
            content = it.content,
            category = it.category,
            tags = Json.decodeFromString(it.tags),
            relatedEntries = Json.decodeFromString(it.relatedEntries),
            createdAt = Instant.parse(it.createdAt),
            updatedAt =Instant.parse(it.updatedAt)
        )
    }



    override fun getAllLoreEntries(): List<Lore> {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.getAllLoreEntries().executeAsList().map(mapper)
        } catch (e: Exception) {
            println("Error getting all lore entries: ${e.message}")
            emptyList()
        }
    }

    override fun getLoreEntryById(id: String): Lore? {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.getLoreEntryById(id).executeAsOneOrNull()?.let { mapper.invoke(it) }

        } catch (e: Exception) {
            println("Error getting lore entry by ID: ${e.message}")
            null
        }
    }

    override fun insertLoreEntry(
        id: String,
        campaignId: String,
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        relatedEntries: List<String>,
        createdAt: Instant,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.insertLoreEntry(
                id = id,
                campaignId = campaignId,
                title = title,
                content = content,
                category = category,
                tags = Json.encodeToString(tags),
                relatedEntries = Json.encodeToString(relatedEntries),
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()
            )
        } catch (e: Exception) {
            println("Error inserting lore entry: ${e.message}")
        }
    }

    override fun updateLoreEntry(
        id: String,
        title: String,
        content: String,
        category: String,
        tags: List<String>,
        relatedEntries: List<String>,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.updateLoreEntry(
                title = title,
                content = content,
                category = category,
                tags = Json.encodeToString(tags),
                relatedEntries = Json.encodeToString(relatedEntries),
                updatedAt = updatedAt.toString(),
                id = id
            )
        } catch (e: Exception) {
            println("Error updating lore entry: ${e.message}")
        }
    }

    override fun deleteLoreEntry(id: String) {
        try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.deleteLoreEntry(id)
        } catch (e: Exception) {
            println("Error deleting lore entry: ${e.message}")
        }
    }

    override fun searchLoreEntries(query: String): List<Lore> {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.searchLoreEntries(query).executeAsList().map(mapper)
        } catch (e: Exception) {
            println("Error searching lore entries: ${e.message}")
            emptyList()
        }
    }

    override fun searchLoreEntriesLike(query: String): List<Lore> {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.searchLoreEntriesLike(query).executeAsList().map(mapper)
        } catch (e: Exception) {
            println("Error searching lore entries with LIKE: ${e.message}")
            emptyList()
        }
    }

    override fun getLoreEntriesByCategory(category: String): List<Lore> {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.getLoreEntriesByCategory(category).executeAsList().map(mapper)
        } catch (e: Exception) {
            println("Error getting lore entries by category: ${e.message}")
            emptyList()
        }
    }

    override fun getLoreEntriesByTag(tag: String): List<Lore> {
        return try {
            val db = databaseProvider.getDatabase()
            db.loreQueries.getLoreEntriesByTag(tag).executeAsList().map(mapper)
        } catch (e: Exception) {
            println("Error getting lore entries by tag: ${e.message}")
            emptyList()
        }
    }
}