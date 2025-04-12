package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.location.Location
import net.tactware.worldweaver.dal.repository.LocationRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving locations by party members presence.
 * Following the single responsibility principle, this use case only handles retrieving locations
 * based on whether they have party members present.
 */
@Factory
class GetLocationsByPartyMembersUseCase(private val locationRepository: LocationRepository) {

    /**
     * Retrieves locations based on whether they have party members present.
     * 
     * @param hasPartyMembers Whether to retrieve locations with party members (true) or without (false)
     * @return A list of locations that match the criteria
     */
    fun execute(hasPartyMembers: Boolean): List<Location> {
        return locationRepository.getLocationsByPartyMembers(hasPartyMembers)
    }
}