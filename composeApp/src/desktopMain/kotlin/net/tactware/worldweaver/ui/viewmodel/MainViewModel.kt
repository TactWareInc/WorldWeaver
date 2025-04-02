package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.tactware.worldweaver.bl.CampaignService
import org.koin.core.annotation.Factory

/**
 * Main ViewModel for the application following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 */
@Factory
class MainViewModel(
    private val campaignService: CampaignService
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
}
