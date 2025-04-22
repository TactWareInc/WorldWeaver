package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.location.Location
import net.tactware.worldweaver.dal.repository.LocationRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving locations by campaign ID.
 * Following the single responsibility principle, this use case only handles retrieving locations for a specific campaign.
 */
@Factory
class GetLocationsByCampaignUseCase(private val locationRepository: LocationRepository) {

    /**
     * Retrieves locations for a specific campaign.
     * 
     * @param campaignId The ID of the campaign to retrieve locations for
     * @return A list of locations for the specified campaign
     */
    fun execute(campaignId: String): List<Location> {
        return locationRepository.getLocationsByCampaignId(campaignId)
    }
}