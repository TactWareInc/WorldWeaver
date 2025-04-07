package net.tactware.worldweaver.bl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import net.tactware.worldweaver.dal.model.Campaign
import net.tactware.worldweaver.dal.model.GameMechanics
import net.tactware.worldweaver.dal.repository.CampaignRepository
import org.koin.core.annotation.Single

/**
 * Singleton service for handling campaign information across the application.
 * Manages campaigns, sessions, quests, and story arcs.
 */
@Single
class CampaignService(
    private val campaignRepository: CampaignRepository
) {
    // In-memory storage for campaigns
    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())

    // Public read-only access to campaigns as a Flow
    val campaignsFlow: StateFlow<List<Campaign>> = _campaigns.asStateFlow()
    
    // For backward compatibility
    val campaigns: List<Campaign>
        get() = _campaigns.value

    // Track the active campaign ID
    private val _activeCampaignId = MutableStateFlow<String?>(null)

    // Public read-only access to active campaign ID as a Flow
    val activeCampaignIdFlow: StateFlow<String?> = _activeCampaignId.asStateFlow()
    
    // For backward compatibility
    val activeCampaignId: String?
        get() = _activeCampaignId.value

    // Public access to the active campaign as a Flow
    val activeCampaignFlow: Flow<Campaign?> = combine(_activeCampaignId, _campaigns) { id, campaigns ->
        id?.let { campaignId -> campaigns.find { it.id == campaignId } }
    }
    
    // For backward compatibility
    val activeCampaign: Campaign?
        get() = _activeCampaignId.value?.let { id -> _campaigns.value.find { it.id == id } }

    // Initialize by loading campaigns from the database
    init {
        // Load campaigns from the database
        loadCampaignsFromDatabase()

        // If no campaigns exist, create a sample campaign
        if (_campaigns.value.isEmpty()) {
            val id = addCampaign(
                name = "The Shadow of Malachar",
                description = "Five hundred years after the Great War, the descendants of the Lich King Malachar seek to resurrect their master and plunge the world into darkness once more.",
                setting = "The Kingdom of Aldoria and surrounding lands",
                playerCharacters = listOf("Thorne Ironheart", "Elara Nightshade", "Grimble Woodfoot"),
                activeQuests = listOf(
                    "The Awakening Darkness: Investigate undead sightings near Ravenhollow",
                    "The Missing Blacksmith: Find the missing blacksmith of Ravenhollow"
                ),
                notes = "Campaign started on October 15, 2023. Players are currently in the village of Ravenhollow investigating reports of undead activity."
            )
            // Set the first campaign as active by default
            setActiveCampaign(id)
        } else if (_activeCampaignId.value == null && _campaigns.value.isNotEmpty()) {
            // Set the first campaign as active by default if none is active
            setActiveCampaign(_campaigns.value.first().id)
        }
    }

    /**
     * Loads campaigns from the database
     */
    private fun loadCampaignsFromDatabase() {
        try {
            val campaigns = campaignRepository.getAllCampaigns()
            _campaigns.value = campaigns
        } catch (e: Exception) {
            // Log the error
            println("Error loading campaigns from database: ${e.message}")
        }
    }

    /**
     * Adds a new campaign
     */
    fun addCampaign(
        name: String,
        description: String,
        setting: String,
        playerCharacters: List<String> = emptyList(),
        activeQuests: List<String> = emptyList(),
        completedQuests: List<String> = emptyList(),
        notes: String = "",
        mechanics: GameMechanics = GameMechanics.FIFTH_EDITION
    ): String {
        val campaign = Campaign(
            name = name,
            description = description,
            setting = setting,
            playerCharacters = playerCharacters,
            activeQuests = activeQuests,
            completedQuests = completedQuests,
            notes = notes,
            mechanics = mechanics
        )

        // Add to in-memory storage
        _campaigns.value = _campaigns.value + campaign

        // Save to database
        try {
            campaignRepository.insertCampaign(
                id = campaign.id,
                name = campaign.name,
                description = campaign.description,
                setting = campaign.setting,
                playerCharacters = campaign.playerCharacters,
                activeQuests = campaign.activeQuests,
                completedQuests = campaign.completedQuests,
                notes = campaign.notes,
                mechanics = campaign.mechanics,
                createdAt = campaign.createdAt,
                updatedAt = campaign.updatedAt
            )
        } catch (e: Exception) {
            // Log the error
            println("Error saving campaign to database: ${e.message}")
        }

        return campaign.id
    }

    /**
     * Updates an existing campaign
     */
    fun updateCampaign(
        id: String,
        name: String? = null,
        description: String? = null,
        setting: String? = null,
        playerCharacters: List<String>? = null,
        activeQuests: List<String>? = null,
        completedQuests: List<String>? = null,
        notes: String? = null,
        mechanics: GameMechanics? = null
    ) {
        val currentCampaigns = _campaigns.value
        val index = currentCampaigns.indexOfFirst { it.id == id }
        if (index != -1) {
            val campaign = currentCampaigns[index]
            val updatedCampaign = campaign.copy(
                name = name ?: campaign.name,
                description = description ?: campaign.description,
                setting = setting ?: campaign.setting,
                playerCharacters = playerCharacters ?: campaign.playerCharacters,
                activeQuests = activeQuests ?: campaign.activeQuests,
                completedQuests = completedQuests ?: campaign.completedQuests,
                notes = notes ?: campaign.notes,
                mechanics = mechanics ?: campaign.mechanics,
                updatedAt = Clock.System.now()
            )

            // Update in-memory storage
            val updatedCampaigns = currentCampaigns.toMutableList()
            updatedCampaigns[index] = updatedCampaign
            _campaigns.value = updatedCampaigns

            // Update in database
            try {
                campaignRepository.updateCampaign(
                    id = updatedCampaign.id,
                    name = updatedCampaign.name,
                    description = updatedCampaign.description,
                    setting = updatedCampaign.setting,
                    playerCharacters = updatedCampaign.playerCharacters,
                    activeQuests = updatedCampaign.activeQuests,
                    completedQuests = updatedCampaign.completedQuests,
                    notes = updatedCampaign.notes,
                    mechanics = updatedCampaign.mechanics,
                    updatedAt = updatedCampaign.updatedAt
                )
            } catch (e: Exception) {
                // Log the error
                println("Error updating campaign in database: ${e.message}")
            }
        }
    }

    /**
     * Removes a campaign
     */
    fun removeCampaign(id: String) {
        // Remove from in-memory storage
        _campaigns.value = _campaigns.value.filter { it.id != id }

        // Remove from database
        try {
            campaignRepository.deleteCampaign(id)
        } catch (e: Exception) {
            // Log the error
            println("Error removing campaign from database: ${e.message}")
        }
    }

    /**
     * Searches campaigns using Full-Text Search
     */
    fun searchCampaigns(query: String): List<Campaign> {
        // Prepare the query for FTS
        val ftsQuery = query.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" OR ") { it.trim() }

        // First try to search in the database using FTS
        try {
            val results = campaignRepository.searchCampaigns(ftsQuery)
            if (results.isNotEmpty()) {
                return results
            }

            // If FTS returns no results, try the legacy LIKE search as fallback
            val likeResults = campaignRepository.searchCampaignsLike(query)
            if (likeResults.isNotEmpty()) {
                return likeResults
            }
        } catch (e: Exception) {
            // Log the error
            println("Error searching campaigns in database: ${e.message}")
        }

        // Fall back to in-memory search if database search fails or returns no results
        val lowercaseQuery = query.lowercase()
        return _campaigns.value.filter { 
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) ||
            it.setting.lowercase().contains(lowercaseQuery) ||
            it.notes.lowercase().contains(lowercaseQuery)
        }
    }

    /**
     * Sets the active campaign by ID
     */
    fun setActiveCampaign(id: String?) {
        // Only set if the campaign exists or id is null (to clear active campaign)
        if (id == null || _campaigns.value.any { it.id == id }) {
            _activeCampaignId.value = id
        }
    }
    
    /**
     * Gets a campaign by ID
     */
    fun getCampaignById(id: String): Campaign? {
        return _campaigns.value.find { it.id == id }
    }
    
    /**
     * Gets a campaign by ID as a Flow
     */
    fun getCampaignByIdFlow(id: String): Flow<Campaign?> {
        return _campaigns.map { campaigns -> campaigns.find { it.id == id } }
    }
}