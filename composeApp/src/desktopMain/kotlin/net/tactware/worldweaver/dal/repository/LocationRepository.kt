package net.tactware.worldweaver.dal.repository

import kotlinx.datetime.Instant
import net.tactware.worldweaver.dal.model.location.Location

/**
 * Repository interface for Location data access.
 * Defines methods for CRUD operations and search functionality.
 */
interface LocationRepository {
    /**
     * Get all locations ordered by updated date (descending)
     */
    fun getAllLocations(): List<Location>

    /**
     * Get a location by ID
     */
    fun getLocationById(id: String): Location?

    /**
     * Insert a new location
     */
    fun insertLocation(
        id: String,
        campaignId: String,
        name: String,
        description: String,
        language: String,
        dialect: String,
        climate: String,
        terrain: String,
        population: String,
        government: String,
        economy: String,
        religion: String,
        landmarks: List<String>,
        history: String,
        notes: String,
        hasPartyMembers: Boolean,
        createdAt: Instant,
        updatedAt: Instant
    )

    /**
     * Update an existing location
     */
    fun updateLocation(
        id: String,
        name: String,
        description: String,
        language: String,
        dialect: String,
        climate: String,
        terrain: String,
        population: String,
        government: String,
        economy: String,
        religion: String,
        landmarks: List<String>,
        history: String,
        notes: String,
        hasPartyMembers: Boolean,
        updatedAt: Instant
    )

    /**
     * Delete a location by ID
     */
    fun deleteLocation(id: String)

    /**
     * Search locations using Full-Text Search
     */
    fun searchLocations(query: String): List<Location>

    /**
     * Search locations using LIKE (fallback)
     */
    fun searchLocationsLike(query: String): List<Location>

    /**
     * Get locations by campaign ID
     */
    fun getLocationsByCampaignId(campaignId: String): List<Location>

    /**
     * Get locations by language
     */
    fun getLocationsByLanguage(language: String): List<Location>

    /**
     * Get locations by dialect
     */
    fun getLocationsByDialect(dialect: String): List<Location>

    /**
     * Get locations by climate
     */
    fun getLocationsByClimate(climate: String): List<Location>

    /**
     * Get locations by terrain
     */
    fun getLocationsByTerrain(terrain: String): List<Location>

    /**
     * Get locations by party members presence
     */
    fun getLocationsByPartyMembers(hasPartyMembers: Boolean): List<Location>
}
