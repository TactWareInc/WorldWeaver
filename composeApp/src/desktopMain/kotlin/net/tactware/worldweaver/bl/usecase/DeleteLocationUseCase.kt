package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.repository.LocationRepository
import org.koin.core.annotation.Factory

/**
 * Use case for deleting a location.
 * Following the single responsibility principle, this use case only handles location deletion.
 */
@Factory
class DeleteLocationUseCase(private val locationRepository: LocationRepository) {

    /**
     * Deletes a location with the specified ID.
     * 
     * @param id The ID of the location to delete
     */
    fun execute(id: String) {
        locationRepository.deleteLocation(id)
    }
}