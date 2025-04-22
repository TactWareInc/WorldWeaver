package net.tactware.worldweaver.bl.usecase

import kotlinx.coroutines.flow.Flow
import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.repository.CharacterRepository
import org.koin.core.annotation.Factory

/**
 * Use case for retrieving all characters.
 * Following the single responsibility principle, this use case only handles retrieving all characters.
 */
@Factory
class GetCharactersUseCase(private val characterRepository: CharacterRepository) {

    /**
     * Retrieves all characters ordered by updated date (descending).
     * 
     * @return List of all characters
     */
    fun execute(): List<Character> {
        return characterRepository.getAllCharacters()
    }

    /**
     * Retrieves a flow of all characters ordered by updated date (descending).
     * 
     * @return Flow of all characters
     */
    fun executeFlow(): Flow<List<Character>> {
        return characterRepository.getCharactersFlow()
    }
}
