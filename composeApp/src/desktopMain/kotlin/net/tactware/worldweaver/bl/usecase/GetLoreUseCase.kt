package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.lore.Lore
import net.tactware.worldweaver.dal.repository.LoreRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving all lore entries.
 * Following the single responsibility principle, this use case only handles retrieving all lore entries.
 */
@Factory
class GetLoreUseCase(private val loreRepository: LoreRepository) {

    /**
     * Retrieves all lore entries.
     * 
     * @return A list of all lore entries
     */
    fun execute(): List<Lore> {
        return loreRepository.getAllLoreEntries()
    }
}