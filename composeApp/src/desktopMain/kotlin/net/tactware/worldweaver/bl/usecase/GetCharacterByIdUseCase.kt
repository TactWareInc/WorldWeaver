package net.tactware.worldweaver.bl.usecase

import kotlinx.coroutines.flow.Flow
import net.tactware.worldweaver.dal.repository.CharacterRepository
import net.tactware.worldweaver.dal.model.character.Character
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving a character by ID.
 * Following the single responsibility principle, this use case only handles retrieving a specific character.
 */
@Factory
class GetCharacterByIdUseCase(private val characterRepository: CharacterRepository) {

    /**
     * Retrieves a character by its ID as a Flow.
     * 
     * @param id The ID of the character to retrieve
     * @return A Flow emitting the character with the specified ID, or null if not found
     */
    operator fun invoke(id: String): Character? {
        return characterRepository.getCharacterById(id)
    }
}
