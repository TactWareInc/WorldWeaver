package net.tactware.worldweaver.ui.viewmodel

import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.model.character.CharacterRelationship
import net.tactware.worldweaver.dal.model.character.CharacterType

/**
 * Sealed class representing all possible user interactions with the characters screen.
 * This follows a hierarchical structure to allow for more specific handling of different
 * types of interactions, particularly data entry.
 */
sealed class CharacterInteraction {
    /**
     * Select a character to view details.
     */
    data class SelectCharacter(val characterId: String?) : CharacterInteraction()

    /**
     * Filter characters by type.
     */
    data class FilterByType(val type: CharacterType?) : CharacterInteraction()

    /**
     * Show the form to create a new character.
     */
    object ShowNewCharacterForm : CharacterInteraction()

    /**
     * Hide the form to create a new character.
     */
    object HideNewCharacterForm : CharacterInteraction()

    /**
     * Start editing an existing character.
     */
    data class StartEditingCharacter(val characterId: String) : CharacterInteraction()

    /**
     * Cancel editing a character.
     */
    object CancelEditing : CharacterInteraction()

    /**
     * Delete a character.
     */
    data class DeleteCharacter(val characterId: String) : CharacterInteraction()

    /**
     * Nested sealed class for data entry interactions.
     * This allows for more specific handling of data entry operations
     * and validation of required fields before creating or updating a character.
     */
    sealed class DataEntry : CharacterInteraction() {
        /**
         * Data entry for character name.
         */
        data class EnteredName(val text: String) : DataEntry()

        /**
         * Data entry for character type.
         */
        data class EnteredType(val type: CharacterType) : DataEntry()

        /**
         * Data entry for character race.
         */
        data class EnteredRace(val text: String) : DataEntry()

        /**
         * Data entry for character class.
         */
        data class EnteredClass(val text: String) : DataEntry()

        /**
         * Data entry for character subclass.
         */
        data class EnteredSubclass(val text: String) : DataEntry()

        /**
         * Data entry for character level.
         */
        data class EnteredLevel(val value: Int?) : DataEntry()

        /**
         * Data entry for character hit points.
         */
        data class EnteredHitPoints(val value: Int?) : DataEntry()

        /**
         * Data entry for character max hit points.
         */
        data class EnteredMaxHitPoints(val value: Int?) : DataEntry()

        /**
         * Data entry for character armor class.
         */
        data class EnteredArmorClass(val value: Int?) : DataEntry()

        /**
         * Data entry for character lineage.
         */
        data class EnteredLineage(val text: String) : DataEntry()

        /**
         * Data entry for character relationships.
         */
        data class EnteredRelationships(val relationships: List<CharacterRelationship>) : DataEntry()

        /**
         * Data entry for character description.
         */
        data class EnteredDescription(val text: String) : DataEntry()

        /**
         * Data entry for character notes.
         */
        data class EnteredNotes(val text: String) : DataEntry()

        /**
         * Submit the form to create a new character.
         */
        object SubmitCreate : DataEntry()

        /**
         * Submit the form to update an existing character.
         */
        data class SubmitUpdate(val id: String) : DataEntry()
    }
}

/**
 * Extension function to process a CharacterInteraction in the ViewModel.
 * This is kept for backward compatibility.
 */
fun CharacterViewModel.processInteraction(interaction: CharacterInteraction) {
    // Directly call the onInteraction method
    onInteraction(interaction)
}
