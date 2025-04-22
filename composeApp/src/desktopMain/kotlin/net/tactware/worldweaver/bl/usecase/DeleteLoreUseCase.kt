package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.repository.LoreRepository
import org.koin.core.annotation.Factory

/**
 * Use case for deleting a lore entry.
 * Following the single responsibility principle, this use case only handles lore entry deletion.
 */
@Factory
class DeleteLoreUseCase(private val loreRepository: LoreRepository) {

    /**
     * Deletes a lore entry with the specified ID.
     * 
     * @param id The ID of the lore entry to delete
     */
    fun execute(id: String) {
        loreRepository.deleteLoreEntry(id)
    }
}