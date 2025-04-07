package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.Campaign
import net.tactware.worldweaver.dal.model.GameMechanics
import org.koin.core.annotation.Factory

/**
 * ViewModel for the Campaign screen following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 */
@Factory
class CampaignViewModel(
    private val campaignService: CampaignService
) : ViewModel() {

    // UI State
    var state by mutableStateOf(CampaignScreenState())
        private set

    // Expose campaigns from service
    val campaigns: StateFlow<List<Campaign>> = campaignService.campaignsFlow

    init {
        // Initialize state
    }

    /**
     * Handle user interactions following the MVI pattern.
     * This is the central function for processing all user actions.
     */
    fun onInteraction(action: CampaignScreenAction) {
        when (action) {
            is CampaignScreenAction.ShowNewCampaignForm -> {
                state = state.copy(
                    showNewCampaignForm = true,
                    editingCampaignId = null,
                    // Reset form fields
                    campaignName = "",
                    campaignDescription = "",
                    campaignSetting = "",
                    campaignNotes = "",
                    campaignMechanics = GameMechanics.FIFTH_EDITION // Default to 5E
                )
            }
            is CampaignScreenAction.HideNewCampaignForm -> {
                state = state.copy(
                    showNewCampaignForm = false,
                    // Reset form fields
                    campaignName = "",
                    campaignDescription = "",
                    campaignSetting = "",
                    campaignNotes = "",
                    campaignMechanics = GameMechanics.FIFTH_EDITION // Default to 5E
                )
            }
            is CampaignScreenAction.StartEditingCampaign -> {
                // Find the campaign to edit
                val campaign = getCampaignById(action.campaignId)
                if (campaign != null) {
                    // Update state with campaign data
                    state = state.copy(
                        editingCampaignId = action.campaignId,
                        showNewCampaignForm = false,
                        campaignName = campaign.name,
                        campaignDescription = campaign.description,
                        campaignSetting = campaign.setting,
                        campaignNotes = campaign.notes,
                        campaignMechanics = campaign.mechanics
                    )
                }
            }
            is CampaignScreenAction.CancelEditing -> {
                state = state.copy(
                    editingCampaignId = null,
                    // Reset form fields
                    campaignName = "",
                    campaignDescription = "",
                    campaignSetting = "",
                    campaignNotes = "",
                    campaignMechanics = GameMechanics.FIFTH_EDITION // Default to 5E
                )
            }
            is CampaignScreenAction.CreateCampaign -> {
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
                state = state.copy(
                    showNewCampaignForm = false,
                    // Reset form fields
                    campaignName = "",
                    campaignDescription = "",
                    campaignSetting = "",
                    campaignNotes = ""
                )
            }
            is CampaignScreenAction.UpdateCampaign -> {
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
                state = state.copy(
                    editingCampaignId = null,
                    // Reset form fields
                    campaignName = "",
                    campaignDescription = "",
                    campaignSetting = "",
                    campaignNotes = ""
                )
            }
            is CampaignScreenAction.DeleteCampaign -> {
                campaignService.removeCampaign(action.campaignId)
                // If the deleted campaign was active, clear the active campaign
                if (campaignService.activeCampaignId == action.campaignId) {
                    campaignService.setActiveCampaign(null)
                }
            }
            is CampaignScreenAction.SetActiveCampaign -> {
                campaignService.setActiveCampaign(action.campaignId)
            }
            is CampaignScreenAction.SelectCampaign -> {
                state = state.copy(selectedCampaignId = action.campaignId)
            }
            is CampaignScreenAction.UpdateCampaignName -> {
                state = state.copy(campaignName = action.name)
            }
            is CampaignScreenAction.UpdateCampaignDescription -> {
                state = state.copy(campaignDescription = action.description)
            }
            is CampaignScreenAction.UpdateCampaignSetting -> {
                state = state.copy(campaignSetting = action.setting)
            }
            is CampaignScreenAction.UpdateCampaignNotes -> {
                state = state.copy(campaignNotes = action.notes)
            }
            is CampaignScreenAction.UpdateCampaignMechanics -> {
                state = state.copy(campaignMechanics = action.mechanics)
            }
        }
    }

    /**
     * Gets the active campaign
     */
    fun getActiveCampaign(): Campaign? {
        return campaignService.activeCampaign
    }

    /**
     * Gets a campaign by ID
     */
    fun getCampaignById(id: String): Campaign? {
        return campaignService.getCampaignById(id)
    }
}

/**
 * Represents the UI state for the campaign screen.
 */
data class CampaignScreenState(
    val showNewCampaignForm: Boolean = false,
    val editingCampaignId: String? = null,
    // Form fields
    val campaignName: String = "",
    val campaignDescription: String = "",
    val campaignSetting: String = "",
    val campaignNotes: String = "",
    val campaignMechanics: GameMechanics = GameMechanics.FIFTH_EDITION, // Default to 5E
    // Selected campaign for detail view
    val selectedCampaignId: String? = null
)

/**
 * Sealed class representing all possible user interactions with the campaign screen.
 */
sealed class CampaignScreenAction {
    /**
     * Show the form to create a new campaign.
     */
    object ShowNewCampaignForm : CampaignScreenAction()

    /**
     * Hide the form to create a new campaign.
     */
    object HideNewCampaignForm : CampaignScreenAction()

    /**
     * Start editing an existing campaign.
     */
    data class StartEditingCampaign(val campaignId: String) : CampaignScreenAction()

    /**
     * Cancel editing a campaign.
     */
    object CancelEditing : CampaignScreenAction()

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
        val notes: String = "",
        val mechanics: GameMechanics = GameMechanics.FIFTH_EDITION // Default to 5E
    ) : CampaignScreenAction()

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
        val notes: String? = null,
        val mechanics: GameMechanics? = null
    ) : CampaignScreenAction()

    /**
     * Delete a campaign.
     */
    data class DeleteCampaign(val campaignId: String) : CampaignScreenAction()

    /**
     * Set a campaign as active.
     */
    data class SetActiveCampaign(val campaignId: String) : CampaignScreenAction()

    /**
     * Select a campaign for detail view.
     */
    data class SelectCampaign(val campaignId: String?) : CampaignScreenAction()

    /**
     * Update campaign name field.
     */
    data class UpdateCampaignName(val name: String) : CampaignScreenAction()

    /**
     * Update campaign description field.
     */
    data class UpdateCampaignDescription(val description: String) : CampaignScreenAction()

    /**
     * Update campaign setting field.
     */
    data class UpdateCampaignSetting(val setting: String) : CampaignScreenAction()

    /**
     * Update campaign notes field.
     */
    data class UpdateCampaignNotes(val notes: String) : CampaignScreenAction()

    /**
     * Update campaign mechanics field.
     */
    data class UpdateCampaignMechanics(val mechanics: GameMechanics) : CampaignScreenAction()
}