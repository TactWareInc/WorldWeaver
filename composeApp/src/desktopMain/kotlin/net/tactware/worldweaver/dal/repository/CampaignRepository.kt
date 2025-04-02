package net.tactware.worldweaver.dal.repository

import kotlinx.datetime.Instant
import net.tactware.worldweaver.dal.model.Campaign

/**
 * Repository interface for Campaign data access.
 * Defines methods for CRUD operations and search functionality.
 */
interface CampaignRepository {
    /**
     * Get all campaigns ordered by updated date (descending)
     */
    fun getAllCampaigns(): List<Campaign>

    /**
     * Get a campaign by ID
     */
    fun getCampaignById(id: String): Campaign?

    /**
     * Insert a new campaign
     */
    fun insertCampaign(
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
    )

    /**
     * Update an existing campaign
     */
    fun updateCampaign(
        id: String,
        name: String,
        description: String,
        setting: String,
        playerCharacters: List<String>,
        activeQuests: List<String>,
        completedQuests: List<String>,
        notes: String,
        updatedAt: Instant
    )

    /**
     * Delete a campaign by ID
     */
    fun deleteCampaign(id: String)

    /**
     * Search campaigns using Full-Text Search
     */
    fun searchCampaigns(query: String): List<Campaign>

    /**
     * Search campaigns using LIKE (fallback)
     */
    fun searchCampaignsLike(query: String): List<Campaign>

    /**
     * Specialized search methods
     */
    fun searchCampaignsByName(query: String): List<Campaign>
    fun searchCampaignsByDescription(query: String): List<Campaign>
    fun searchCampaignsBySetting(query: String): List<Campaign>
    fun searchCampaignsByNotes(query: String): List<Campaign>
}
