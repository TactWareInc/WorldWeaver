package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.lore.Lore
import net.tactware.worldweaver.dal.repository.LoreRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving a lore entry by ID.
 * Following the single responsibility principle, this use case only handles retrieving a specific lore entry.
 */
@Factory
class GetLoreByIdUseCase(private val loreRepository: LoreRepository) {

    /**
     * Retrieves a lore entry by its ID.
     * 
     * @param id The ID of the lore entry to retrieve
     * @return The lore entry with the specified ID, or null if not found
     */
    fun execute(id: String): Lore? {
        return loreRepository.getLoreEntryById(id)
    }
}