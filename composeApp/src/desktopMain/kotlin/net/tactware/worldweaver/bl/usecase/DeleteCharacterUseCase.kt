package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.repository.CharacterRepository
import org.koin.core.annotation.Factory

/**
 * Use case for deleting a character.
 * Following the single responsibility principle, this use case only handles character deletion.
 */
@Factory
class DeleteCharacterUseCase(private val characterRepository: CharacterRepository) {

    /**
     * Deletes a character with the specified ID.
     * 
     * @param id The ID of the character to delete
     */
    fun execute(id: String) {
        characterRepository.deleteCharacter(id)
    }
}