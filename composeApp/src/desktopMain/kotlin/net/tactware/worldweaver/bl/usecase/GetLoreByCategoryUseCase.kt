package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.lore.Lore
import net.tactware.worldweaver.dal.repository.LoreRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving lore entries by category.
 * Following the single responsibility principle, this use case only handles retrieving lore entries by category.
 */
@Factory
class GetLoreByCategoryUseCase(private val loreRepository: LoreRepository) {

    /**
     * Retrieves lore entries by category.
     * 
     * @param category The category to filter by
     * @return A list of lore entries in the specified category
     */
    fun execute(category: String): List<Lore> {
        return loreRepository.getLoreEntriesByCategory(category)
    }
}