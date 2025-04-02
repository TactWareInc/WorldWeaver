package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.bl.CharacterService
import org.koin.core.annotation.Factory

/**
 * Main ViewModel for the application following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 */
@Factory
class MainViewModel(
    private val campaignService: CampaignService,
    private val characterService: CharacterService
) : ViewModel() {

    // UI State
    var state by mutableStateOf(MainScreenState())
        private set

    /**
     * Handle user interactions following the MVI pattern.
     * This is the central function for processing all user actions.
     */
    fun onInteraction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.NavigateTo -> {
                state = state.copy(selectedNavItem = action.navItem)
            }
            is MainScreenAction.ToggleNavigation -> {
                state = state.copy(
                    causeNavigationToExpand = !state.causeNavigationToExpand
                )
            }
            is MainScreenAction.SetActiveCampaign -> {
                campaignService.setActiveCampaign(action.campaignId)
            }
            is MainScreenAction.UpdateNavTitles -> {
                state = state.copy(showNavItemTitles = action.show)
            }
            is MainScreenAction.UpdateExpandColumn -> {
                state = state.copy(expandColumn = action.expand)
            }
            is MainScreenAction.CreateCampaign -> {
                val campaignId = campaignService.addCampaign(
                    name = action.name,
                    description = action.description,
                    setting = action.setting,
                    playerCharacters = action.playerCharacters,
                    activeQuests = action.activeQuests,
                    completedQuests = action.completedQuests,
                    notes = action.notes
                )
                // Set the newly created campaign as active
                campaignService.setActiveCampaign(campaignId)
            }
            is MainScreenAction.UpdateCampaign -> {
                campaignService.updateCampaign(
                    id = action.id,
                    name = action.name,
                    description = action.description,
                    setting = action.setting,
                    playerCharacters = action.playerCharacters,
                    activeQuests = action.activeQuests,
                    completedQuests = action.completedQuests,
                    notes = action.notes
                )
            }
            is MainScreenAction.DeleteCampaign -> {
                campaignService.removeCampaign(action.campaignId)
                // If the deleted campaign was active, clear the active campaign
                if (campaignService.activeCampaignId == action.campaignId) {
                    campaignService.setActiveCampaign(null)
                }
            }
            is MainScreenAction.CreateCharacter -> {
                characterService.addCharacter(
                    name = action.name,
                    type = action.type,
                    race = action.race,
                    characterClass = action.characterClass,
                    subclass = action.subclass,
                    level = action.level,
                    experiencePoints = action.experiencePoints,
                    abilityScores = action.abilityScores,
                    hitPoints = action.hitPoints,
                    maxHitPoints = action.maxHitPoints,
                    armorClass = action.armorClass,
                    background = action.background,
                    alignmentEnum = action.alignment,
                    description = action.description,
                    notes = action.notes
                )
            }
            is MainScreenAction.UpdateCharacter -> {
                characterService.updateCharacter(
                    id = action.id,
                    name = action.name,
                    type = action.type,
                    race = action.race,
                    characterClass = action.characterClass,
                    subclass = action.subclass,
                    level = action.level,
                    experiencePoints = action.experiencePoints,
                    abilityScores = action.abilityScores,
                    hitPoints = action.hitPoints,
                    maxHitPoints = action.maxHitPoints,
                    armorClass = action.armorClass,
                    background = action.background,
                    alignmentEnum = action.alignment,
                    description = action.description,
                    notes = action.notes
                )
            }
            is MainScreenAction.DeleteCharacter -> {
                characterService.removeCharacter(action.characterId)
            }
        }
    }

    init {
        // Initialize state with data from services
        state = MainScreenState(
            selectedNavItem = 0,
            causeNavigationToExpand = false,
            showNavItemTitles = false,
            expandColumn = false
        )
    }
}

/**
 * Represents the UI state for the main screen.
 */
data class MainScreenState(
    val selectedNavItem: Int = 0,
    val causeNavigationToExpand: Boolean = false,
    val showNavItemTitles: Boolean = false,
    val expandColumn: Boolean = false
)

/**
 * Sealed class representing all possible user interactions with the main screen.
 */
sealed class MainScreenAction {
    /**
     * Navigate to a specific section.
     */
    data class NavigateTo(val navItem: Int) : MainScreenAction()

    /**
     * Toggle the navigation panel expansion.
     */
    object ToggleNavigation : MainScreenAction()

    /**
     * Set a campaign as active.
     */
    data class SetActiveCampaign(val campaignId: String) : MainScreenAction()

    /**
     * Update the navigation titles visibility.
     */
    data class UpdateNavTitles(val show: Boolean) : MainScreenAction()

    /**
     * Update the column expansion state.
     */
    data class UpdateExpandColumn(val expand: Boolean) : MainScreenAction()

    /**
     * Create a new campaign.
     */
    data class CreateCampaign(
        val name: String,
        val description: String,
        val setting: String,
        val playerCharacters: List<String> = emptyList(),
        val activeQuests: List<String> = emptyList(),
        val completedQuests: List<String> = emptyList(),
        val notes: String = ""
    ) : MainScreenAction()

    /**
     * Update an existing campaign.
     */
    data class UpdateCampaign(
        val id: String,
        val name: String? = null,
        val description: String? = null,
        val setting: String? = null,
        val playerCharacters: List<String>? = null,
        val activeQuests: List<String>? = null,
        val completedQuests: List<String>? = null,
        val notes: String? = null
    ) : MainScreenAction()

    /**
     * Delete a campaign.
     */
    data class DeleteCampaign(val campaignId: String) : MainScreenAction()

    /**
     * Create a new character.
     */
    data class CreateCharacter(
        val name: String,
        val type: CharacterService.CharacterType,
        val race: String,
        val characterClass: String = "",
        val subclass: String = "",
        val level: Int = 1,
        val experiencePoints: Int = 0,
        val abilityScores: Map<CharacterService.Ability, Int> = mapOf(
            CharacterService.Ability.STRENGTH to 10,
            CharacterService.Ability.DEXTERITY to 10,
            CharacterService.Ability.CONSTITUTION to 10,
            CharacterService.Ability.INTELLIGENCE to 10,
            CharacterService.Ability.WISDOM to 10,
            CharacterService.Ability.CHARISMA to 10
        ),
        val hitPoints: Int = 10,
        val maxHitPoints: Int = 10,
        val armorClass: Int = 10,
        val background: String = "",
        val alignment: CharacterService.Alignment = CharacterService.Alignment.TRUE_NEUTRAL,
        val description: String = "",
        val notes: String = ""
    ) : MainScreenAction()

    /**
     * Update an existing character.
     */
    data class UpdateCharacter(
        val id: String,
        val name: String? = null,
        val type: CharacterService.CharacterType? = null,
        val race: String? = null,
        val characterClass: String? = null,
        val subclass: String? = null,
        val level: Int? = null,
        val experiencePoints: Int? = null,
        val abilityScores: Map<CharacterService.Ability, Int>? = null,
        val hitPoints: Int? = null,
        val maxHitPoints: Int? = null,
        val armorClass: Int? = null,
        val background: String? = null,
        val alignment: CharacterService.Alignment? = null,
        val description: String? = null,
        val notes: String? = null
    ) : MainScreenAction()

    /**
     * Delete a character.
     */
    data class DeleteCharacter(val characterId: String) : MainScreenAction()
    }
