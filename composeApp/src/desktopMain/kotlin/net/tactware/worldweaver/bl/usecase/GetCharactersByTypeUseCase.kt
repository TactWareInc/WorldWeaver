package net.tactware.worldweaver.bl.usecase

import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.model.character.CharacterType
import net.tactware.worldweaver.dal.repository.CharacterRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving characters by type.
 * Following the single responsibility principle, this use case only handles retrieving characters of a specific type.
 */
@Factory
class GetCharactersByTypeUseCase(private val characterRepository: CharacterRepository) {

    /**
     * Retrieves characters of a specific type.
     * 
     * @param type The type of characters to retrieve (PLAYER_CHARACTER, NON_PLAYER_CHARACTER, MONSTER)
     * @return List of characters of the specified type
     */
    fun execute(type: CharacterType): List<Character> {
        return characterRepository.getCharactersByType(type)
    }
}