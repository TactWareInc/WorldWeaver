package net.tactware.worldweaver.dal.repository

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import net.tactware.worldweaver.dal.model.Campaign
import net.tactware.worldweaver.dal.db.DatabaseProvider
import org.koin.core.annotation.Single

/**
 * Implementation of the CampaignRepository interface.
 * Handles database operations for Campaign entities.
 */
@Single
class CampaignRepositoryImpl(
    private val databaseProvider: DatabaseProvider
) : CampaignRepository {

    /**
     * Database entity class for Campaign
     */
    private data class CampaignEntity(
        val id: String,
        val name: String,
        val description: String,
        val setting: String,
        val playerCharacters: String, // JSON string
        val activeQuests: String, // JSON string
        val completedQuests: String, // JSON string
        val notes: String,
        val createdAt: String, // ISO-8601 string
        val updatedAt: String // ISO-8601 string
    ) {
        // Convert to domain entity
        fun toDomainEntity(): Campaign {
            return Campaign(
                id = id,
                name = name,
                description = description,
                setting = setting,
                playerCharacters = Json.decodeFromString(playerCharacters),
                activeQuests = Json.decodeFromString(activeQuests),
                completedQuests = Json.decodeFromString(completedQuests),
                notes = notes,
                createdAt = Instant.parse(createdAt),
                updatedAt = Instant.parse(updatedAt)
            )
        }
    }

    /**
     * Convert domain entity to database entity
     */
    private fun Campaign.toDbEntity(): CampaignEntity {
        return CampaignEntity(
            id = id,
            name = name,
            description = description,
            setting = setting,
            playerCharacters = Json.encodeToString(playerCharacters),
            activeQuests = Json.encodeToString(activeQuests),
            completedQuests = Json.encodeToString(completedQuests),
            notes = notes,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }

    override fun getAllCampaigns(): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.getAllCampaigns().executeAsList().map { 
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity() 
            }
        } catch (e: Exception) {
            println("Error getting all campaigns: ${e.message}")
            emptyList()
        }
    }

    override fun getCampaignById(id: String): Campaign? {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.getCampaignById(id).executeAsOneOrNull()?.let {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error getting campaign by ID: ${e.message}")
            null
        }
    }

    override fun insertCampaign(
        id: String,
        name: String,
        description: String,
        setting: String,
        playerCharacters: List<String>,
        activeQuests: List<String>,
        completedQuests: List<String>,
        notes: String,
        createdAt: Instant,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.insertCampaign(
                id = id,
                name = name,
                description = description,
                setting = setting,
                playerCharacters = Json.encodeToString(playerCharacters),
                activeQuests = Json.encodeToString(activeQuests),
                completedQuests = Json.encodeToString(completedQuests),
                notes = notes,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()
            )
        } catch (e: Exception) {
            println("Error inserting campaign: ${e.message}")
        }
    }

    override fun updateCampaign(
        id: String,
        name: String,
        description: String,
        setting: String,
        playerCharacters: List<String>,
        activeQuests: List<String>,
        completedQuests: List<String>,
        notes: String,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.updateCampaign(
                name = name,
                description = description,
                setting = setting,
                playerCharacters = Json.encodeToString(playerCharacters),
                activeQuests = Json.encodeToString(activeQuests),
                completedQuests = Json.encodeToString(completedQuests),
                notes = notes,
                updatedAt = updatedAt.toString(),
                id = id
            )
        } catch (e: Exception) {
            println("Error updating campaign: ${e.message}")
        }
    }

    override fun deleteCampaign(id: String) {
        try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.deleteCampaign(id)
        } catch (e: Exception) {
            println("Error deleting campaign: ${e.message}")
        }
    }

    override fun searchCampaigns(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaigns(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns: ${e.message}")
            emptyList()
        }
    }

    override fun searchCampaignsLike(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaignsLike(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns with LIKE: ${e.message}")
            emptyList()
        }
    }

    override fun searchCampaignsByName(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaignsByName(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns by name: ${e.message}")
            emptyList()
        }
    }

    override fun searchCampaignsByDescription(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaignsByDescription(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns by description: ${e.message}")
            emptyList()
        }
    }

    override fun searchCampaignsBySetting(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaignsBySetting(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns by setting: ${e.message}")
            emptyList()
        }
    }

    override fun searchCampaignsByNotes(query: String): List<Campaign> {
        return try {
            val db = databaseProvider.getDatabase()
            db.campaignQueries.searchCampaignsByNotes(query).executeAsList().map {
                CampaignEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    setting = it.setting,
                    playerCharacters = it.playerCharacters,
                    activeQuests = it.activeQuests,
                    completedQuests = it.completedQuests,
                    notes = it.notes,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                ).toDomainEntity()
            }
        } catch (e: Exception) {
            println("Error searching campaigns by notes: ${e.message}")
            emptyList()
        }
    }
}
