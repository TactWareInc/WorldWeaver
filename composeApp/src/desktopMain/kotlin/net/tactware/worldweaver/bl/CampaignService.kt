package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Singleton service for handling campaign information across the application.
 * Manages campaigns, sessions, quests, and story arcs.
 */
object CampaignService {

    /**
     * Data class representing a campaign
     */
    data class Campaign(
        val id: String = generateId(),
        val name: String,
        val description: String,
        val setting: String,
        val playerCharacters: List<String> = emptyList(), // Names or IDs of player characters
        val activeQuests: List<String> = emptyList(), // Names or IDs of active quests
        val completedQuests: List<String> = emptyList(), // Names or IDs of completed quests
        val notes: String = "",
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now()
    )

    // In-memory storage for campaigns
    private val _campaigns = mutableStateListOf<Campaign>()

    // Public read-only access to campaigns
    val campaigns: SnapshotStateList<Campaign> = _campaigns

    // Initialize with a sample campaign
    init {
        addCampaign(
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
        notes: String = ""
    ): String {
        val id = generateId()
        _campaigns.add(
            Campaign(
                id = id,
                name = name,
                description = description,
                setting = setting,
                playerCharacters = playerCharacters,
                activeQuests = activeQuests,
                completedQuests = completedQuests,
                notes = notes
            )
        )
        return id
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
        notes: String? = null
    ) {
        val index = _campaigns.indexOfFirst { it.id == id }
        if (index != -1) {
            val campaign = _campaigns[index]
            _campaigns[index] = campaign.copy(
                name = name ?: campaign.name,
                description = description ?: campaign.description,
                setting = setting ?: campaign.setting,
                playerCharacters = playerCharacters ?: campaign.playerCharacters,
                activeQuests = activeQuests ?: campaign.activeQuests,
                completedQuests = completedQuests ?: campaign.completedQuests,
                notes = notes ?: campaign.notes,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Removes a campaign
     */
    fun removeCampaign(id: String) {
        _campaigns.removeIf { it.id == id }
    }

    /**
     * Searches campaigns by name or description
     */
    fun searchCampaigns(query: String): List<Campaign> {
        val lowercaseQuery = query.lowercase()
        return _campaigns.filter { 
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) ||
            it.setting.lowercase().contains(lowercaseQuery) ||
            it.notes.lowercase().contains(lowercaseQuery)
        }
    }

    /**
     * Generates a unique ID for a campaign
     */
    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }
}