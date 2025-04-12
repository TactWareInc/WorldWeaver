package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.location.Location
import net.tactware.worldweaver.dal.repository.LocationRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving all locations.
 * Following the single responsibility principle, this use case only handles retrieving all locations.
 */
@Factory
class GetLocationsUseCase(private val locationRepository: LocationRepository) {

    /**
     * Retrieves all locations.
     * 
     * @return A list of all locations
     */
    fun execute(): List<Location> {
        return locationRepository.getAllLocations()
    }
}