package net.tactware.worldweaver.bl.usecase

import kotlinx.datetime.Clock
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.location.Location
import net.tactware.worldweaver.dal.repository.LocationRepository
import net.tactware.worldweaver.util.IdGenerator
import org.koin.core.annotation.Factory

/**
 * Use case for creating or updating a location.
 * This use case handles both creating new locations and updating existing ones
 * based on the provided ID.
 */
@Factory
class CreateUpdateLocationUseCase(
    private val locationRepository: LocationRepository,
    private val campaignService: CampaignService
) {

    /**
     * Creates or updates a location.
     * If a location with the given ID exists, it will be updated.
     * If no location with the given ID exists, a new one will be created.
     * If no ID is provided, a new ID will be generated and a new location will be created.
     * 
     * @param id The ID of the location to save (optional for new entries)
     * @return The ID of the saved location
     */
    fun execute(
        id: String? = null,
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
        landmarks: List<String> = emptyList(),
        history: String,
        notes: String,
        hasPartyMembers: Boolean = false
    ): String {
        val locationId = id ?: IdGenerator.generateLocationId()
        val now = Clock.System.now()

        // Get the current active campaign ID
        val campaignId = campaignService.activeCampaignId
            ?: throw IllegalStateException("No active campaign found. Please select a campaign first.")

        // Check if the location exists
        val existingLocation = locationRepository.getLocationById(locationId)

        if (existingLocation != null) {
            // Update existing location
            locationRepository.updateLocation(
                id = locationId,
                name = name,
                description = description,
                language = language,
                dialect = dialect,
                climate = climate,
                terrain = terrain,
                population = population,
                government = government,
                economy = economy,
                religion = religion,
                landmarks = landmarks,
                history = history,
                notes = notes,
                hasPartyMembers = hasPartyMembers,
                updatedAt = now
            )
        } else {
            // Create new location
            locationRepository.insertLocation(
                id = locationId,
                campaignId = campaignId,
                name = name,
                description = description,
                language = language,
                dialect = dialect,
                climate = climate,
                terrain = terrain,
                population = population,
                government = government,
                economy = economy,
                religion = religion,
                landmarks = landmarks,
                history = history,
                notes = notes,
                hasPartyMembers = hasPartyMembers,
                createdAt = now,
                updatedAt = now
            )
        }

        return locationId
    }
}