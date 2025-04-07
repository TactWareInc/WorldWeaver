package net.tactware.worldweaver.dal.model.campaign

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.tactware.worldweaver.dal.model.GameMechanics
import net.tactware.worldweaver.util.IdGenerator

/**
 * Data class representing a campaign
 */
data class Campaign(
    val id: String = IdGenerator.generateCampaignId(),
    val name: String,
    val description: String,
    val setting: String,
    val playerCharacters: List<String> = emptyList(), // Names or IDs of player characters
    val activeQuests: List<String> = emptyList(), // Names or IDs of active quests
    val completedQuests: List<String> = emptyList(), // Names or IDs of completed quests
    val notes: String = "",
    val mechanics: GameMechanics = GameMechanics.FIFTH_EDITION, // Game mechanics system
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
