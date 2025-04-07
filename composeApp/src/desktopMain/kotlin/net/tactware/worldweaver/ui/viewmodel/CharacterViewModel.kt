package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.bl.usecase.*
import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.model.character.CharacterRelationship
import net.tactware.worldweaver.dal.model.character.CharacterType
import org.koin.core.annotation.Factory

/**
 * ViewModel for the Characters screen following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 * Uses use cases to interact with the repository.
 */
@Factory
class CharacterViewModel(
    private val saveCharacterUseCase: SaveCharacterUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val getCharactersByTypeUseCase: GetCharactersByTypeUseCase,
    private val deleteCharacterUseCase: DeleteCharacterUseCase,
    private val campaignService: CampaignService,
) : ViewModel() {

    // UI State
    var state by mutableStateOf(CharacterScreenState())
        private set

    // Expose characters from repository
    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters: StateFlow<List<Character>> = _characters.asStateFlow()

    internal val activeCampaign = campaignService.activeCampaign

    // Initialize state and characters
    init {
        refreshCharacters()
    }

    /**
     * Refreshes the characters list from the repository
     */
    private fun refreshCharacters() {
        _characters.value = getCharactersUseCase.execute()
    }

    /**
     * Handle interactions using the CharacterInteraction sealed class.
     * This provides structured handling of interactions, particularly for data entry.
     */
    fun onInteraction(interaction: CharacterInteraction) {
        when (interaction) {
            is CharacterInteraction.SelectCharacter -> {
                state = state.copy(selectedCharacterId = interaction.characterId)
            }
            is CharacterInteraction.FilterByType -> {
                state = state.copy(filterType = interaction.type)
            }
            is CharacterInteraction.ShowNewCharacterForm -> {
                state = state.copy(
                    showNewCharacterForm = true,
                    editingCharacterId = null,
                    // Reset all form fields
                    name = "",
                    type = CharacterType.PLAYER_CHARACTER,
                    race = "",
                    characterClass = "",
                    subclass = "",
                    level = 1,
                    hitPoints = 10,
                    maxHitPoints = 10,
                    armorClass = 10,
                    lineage = "",
                    relationships = emptyList(),
                    description = "",
                    notes = "",
                    isFormValid = false
                )
            }
            is CharacterInteraction.HideNewCharacterForm -> {
                state = state.copy(
                    showNewCharacterForm = false
                )
            }
            is CharacterInteraction.StartEditingCharacter -> {
                // Set the editing character ID immediately
                state = state.copy(
                    editingCharacterId = interaction.characterId,
                    showNewCharacterForm = false
                )

                // We'll update the form data when the character is loaded
                // This is handled in the UI by collecting the Flow
            }
            is CharacterInteraction.CancelEditing -> {
                state = state.copy(
                    editingCharacterId = null,
                    // Reset all form fields
                    name = "",
                    type = CharacterType.PLAYER_CHARACTER,
                    race = "",
                    characterClass = "",
                    subclass = "",
                    level = 1,
                    hitPoints = 10,
                    maxHitPoints = 10,
                    armorClass = 10,
                    lineage = "",
                    relationships = emptyList(),
                    description = "",
                    notes = ""
                )
            }
            is CharacterInteraction.DeleteCharacter -> {
                deleteCharacterUseCase.execute(interaction.characterId)
                // If the deleted character was selected, clear the selection
                if (state.selectedCharacterId == interaction.characterId) {
                    state = state.copy(selectedCharacterId = null)
                }
                refreshCharacters()
            }
            // Handle individual field entries
            is CharacterInteraction.DataEntry.EnteredName -> {
                state = state.copy(
                    name = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredType -> {
                state = state.copy(
                    type = interaction.type,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredRace -> {
                state = state.copy(
                    race = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredClass -> {
                state = state.copy(
                    characterClass = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredSubclass -> {
                state = state.copy(
                    subclass = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredLevel -> {
                state = state.copy(
                    level = interaction.value,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredHitPoints -> {
                state = state.copy(
                    hitPoints = interaction.value,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredMaxHitPoints -> {
                state = state.copy(
                    maxHitPoints = interaction.value,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredArmorClass -> {
                state = state.copy(
                    armorClass = interaction.value,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredLineage -> {
                state = state.copy(
                    lineage = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredRelationships -> {
                state = state.copy(
                    relationships = interaction.relationships,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredDescription -> {
                state = state.copy(
                    description = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            is CharacterInteraction.DataEntry.EnteredNotes -> {
                state = state.copy(
                    notes = interaction.text,
                    isFormValid = if (state.editingCharacterId != null) state.isUpdateValid() else state.isCreateValid()
                )
            }
            // Handle form submission
            is CharacterInteraction.DataEntry.SubmitCreate -> {
                if (state.isCreateValid()) {
                    // Use saveCharacterUseCase for creating a new character
                    kotlinx.coroutines.runBlocking {
                        saveCharacterUseCase.execute(
                            id = null, // No ID for new character, will be generated
                            name = state.name,
                            type = state.type,
                            race = state.race,
                            characterClass = state.characterClass,
                            subclass = state.subclass,
                            level = state.level ?: 1,
                            hitPoints = state.hitPoints ?: 10,
                            maxHitPoints = state.maxHitPoints ?: 10,
                            armorClass = state.armorClass ?: 10,
                            description = state.description,
                            notes = state.notes
                        )
                    }
                    state = state.copy(
                        showNewCharacterForm = false,
                        // Reset all form fields
                        name = "",
                        type = CharacterType.PLAYER_CHARACTER,
                        race = "",
                        characterClass = "",
                        subclass = "",
                        level = 1,
                        hitPoints = 10,
                        maxHitPoints = 10,
                        armorClass = 10,
                        lineage = "",
                        relationships = emptyList(),
                        description = "",
                        notes = ""
                    )
                    refreshCharacters()
                }
            }
            is CharacterInteraction.DataEntry.SubmitUpdate -> {
                if (state.isUpdateValid()) {
                    // Use saveCharacterUseCase for updating an existing character
                    kotlinx.coroutines.runBlocking {
                        saveCharacterUseCase.execute(
                            id = interaction.id, // Existing ID for update
                            name = if (state.name.isNotBlank()) state.name else null,
                            type = state.type,
                            race = if (state.race.isNotBlank()) state.race else null,
                            characterClass = if (state.characterClass.isNotBlank()) state.characterClass else null,
                            subclass = if (state.subclass.isNotBlank()) state.subclass else null,
                            level = state.level,
                            hitPoints = state.hitPoints,
                            maxHitPoints = state.maxHitPoints,
                            armorClass = state.armorClass,
                            description = if (state.description.isNotBlank()) state.description else null,
                            notes = if (state.notes.isNotBlank()) state.notes else null
                        )
                    }
                    state = state.copy(
                        editingCharacterId = null,
                        // Reset all form fields
                        name = "",
                        type = CharacterType.PLAYER_CHARACTER,
                        race = "",
                        characterClass = "",
                        subclass = "",
                        level = 1,
                        hitPoints = 10,
                        maxHitPoints = 10,
                        armorClass = 10,
                        lineage = "",
                        relationships = emptyList(),
                        description = "",
                        notes = ""
                    )
                    refreshCharacters()
                }
            }
        }
    }

    /**
     * Gets characters filtered by the selected type
     */
    fun getFilteredCharacters(): List<Character> {
        return when (state.filterType) {
            CharacterType.PLAYER_CHARACTER -> getCharactersByTypeUseCase.execute(CharacterType.PLAYER_CHARACTER)
            CharacterType.NON_PLAYER_CHARACTER -> getCharactersByTypeUseCase.execute(CharacterType.NON_PLAYER_CHARACTER)
            CharacterType.MONSTER -> getCharactersByTypeUseCase.execute(CharacterType.MONSTER)
            null -> getCharactersUseCase.execute()
        }
    }

    /**
     * Gets a character by ID as a Flow
     * 
     * @param id The ID of the character to retrieve
     * @return A Flow emitting the character with the specified ID, or null if not found
     */
    fun getCharacterById(id: String): Flow<Character?> {
        return getCharacterByIdUseCase.execute(id)
    }

    /**
     * Gets the selected character as a Flow
     * 
     * @return A Flow emitting the selected character, or null if no character is selected
     */
    fun getSelectedCharacter(): Flow<Character?> {
        return state.selectedCharacterId?.let { id ->
            getCharacterByIdUseCase.execute(id)
        } ?: flowOf(null)
    }
}

/**
 * Represents the UI state for the characters screen.
 */
data class CharacterScreenState(
    val selectedCharacterId: String? = null,
    val filterType: CharacterType? = null,
    val showNewCharacterForm: Boolean = false,
    val editingCharacterId: String? = null,

    // Individual field values for the form
    val name: String = "",
    val type: CharacterType = CharacterType.PLAYER_CHARACTER,
    val race: String = "",
    val characterClass: String = "",
    val subclass: String = "",
    val level: Int? = 1,
    val hitPoints: Int? = 10,
    val maxHitPoints: Int? = 10,
    val armorClass: Int? = 10,
    val lineage: String = "",
    val relationships: List<CharacterRelationship> = emptyList(),
    val description: String = "",
    val notes: String = "",

    val isFormValid: Boolean = false
) {
    /**
     * Checks if all required fields are present to create a character.
     * @return true if all required fields are present, false otherwise
     */
    fun isCreateValid(): Boolean {
        return name.isNotBlank() && 
               type != null && 
               race.isNotBlank()
    }

    /**
     * Checks if all required fields are present to update a character.
     * For updates, we only need the ID and at least one field to update.
     * @return true if all required fields are present, false otherwise
     */
    fun isUpdateValid(): Boolean {
        // For updates, we need at least the ID and one field to update
        return editingCharacterId != null && (
            name.isNotBlank() || 
            type != null || 
            race.isNotBlank() || 
            characterClass.isNotBlank() || 
            subclass.isNotBlank() || 
            level != null || 
            hitPoints != null || 
            maxHitPoints != null || 
            armorClass != null || 
            lineage.isNotBlank() || 
            relationships.isNotEmpty() || 
            description.isNotBlank() || 
            notes.isNotBlank()
        )
    }
}
