package net.tactware.worldweaver.bl.usecase

import kotlinx.datetime.Clock
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.lore.Lore
import net.tactware.worldweaver.dal.repository.LoreRepository
import net.tactware.worldweaver.util.IdGenerator
import org.koin.core.annotation.Factory

/**
 * Use case for creating or updating a lore entry.
 * This use case handles both creating new lore entries and updating existing ones
 * based on the provided ID.
 */
@Factory
class CreateUpdateLoreUseCase(
    private val loreRepository: LoreRepository,
    private val campaignService: CampaignService
) {

    /**
     * Creates or updates a lore entry.
     * If a lore entry with the given ID exists, it will be updated.
     * If no lore entry with the given ID exists, a new one will be created.
     * If no ID is provided, a new ID will be generated and a new lore entry will be created.
     * 
     * @param id The ID of the lore entry to save (optional for new entries)
     * @return The ID of the saved lore entry
     */
    fun execute(
        id: String? = null,
        title: String,
        content: String,
        category: String,
        tags: List<String> = emptyList(),
        relatedEntries: List<String> = emptyList()
    ): String {
        val loreId = id ?: IdGenerator.generateLoreId()
        val now = Clock.System.now()

        // Get the current active campaign ID
        val campaignId = campaignService.activeCampaignId
            ?: throw IllegalStateException("No active campaign found. Please select a campaign first.")

        // Check if the lore entry exists
        val existingLoreEntry = loreRepository.getLoreEntryById(loreId)

        if (existingLoreEntry != null) {
            // Update existing lore entry
            loreRepository.updateLoreEntry(
                id = loreId,
                title = title,
                content = content,
                category = category,
                tags = tags,
                relatedEntries = relatedEntries,
                updatedAt = now
            )
        } else {
            // Create new lore entry
            loreRepository.insertLoreEntry(
                id = loreId,
                campaignId = campaignId,
                title = title,
                content = content,
                category = category,
                tags = tags,
                relatedEntries = relatedEntries,
                createdAt = now,
                updatedAt = now
            )
        }

        return loreId
    }

}
