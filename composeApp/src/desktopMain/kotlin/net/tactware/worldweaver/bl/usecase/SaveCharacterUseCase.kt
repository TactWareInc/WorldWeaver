package net.tactware.worldweaver.bl.usecase

import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.character.Ability
import net.tactware.worldweaver.dal.model.character.Alignment
import net.tactware.worldweaver.dal.model.character.CharacterType
import net.tactware.worldweaver.dal.model.character.Feature
import net.tactware.worldweaver.dal.model.character.Item
import net.tactware.worldweaver.dal.repository.CharacterRepository
import org.koin.core.annotation.Factory

/**
 * Use case for saving a character (create or update).
 * This use case handles both creating new characters and updating existing ones
 * based on the provided ID.
 */
@Factory
class SaveCharacterUseCase(
    private val characterRepository: CharacterRepository,
    private val campaignService: CampaignService
) {

    /**
     * Saves a character (creates a new one or updates an existing one).
     * If a character with the given ID exists, it will be updated.
     * If no character with the given ID exists, a new one will be created.
     * If no ID is provided, a new ID will be generated and a new character will be created.
     * 
     * @param id The ID of the character to save (optional for new characters)
     * @return The ID of the saved character
     */
    suspend fun execute(
        id: String? = null,
        name: String? = null,
        type: CharacterType? = null,
        race: String? = null,
        characterClass: String? = null,
        subclass: String? = null,
        level: Int? = null,
        experiencePoints: Int? = null,
        abilityScores: Map<Ability, Int>? = null,
        hitPoints: Int? = null,
        maxHitPoints: Int? = null,
        temporaryHitPoints: Int? = null,
        armorClass: Int? = null,
        background: String? = null,
        alignment: Alignment? = null,
        description: String? = null,
        notes: String? = null,
        items: List<Item>? = null,
        currency: Map<String, Int>? = null,
        features: List<Feature>? = null
    ): String {
        val characterId = id ?: generateId()
        val now = Clock.System.now()

        // Get the current active campaign ID
        val campaignId = campaignService.activeCampaignId
            ?: throw IllegalStateException("No active campaign found. Please select a campaign first.")

        // Check if the character exists
        val existingCharacter = characterRepository.getCharacterById(characterId).first()

        if (existingCharacter != null) {
            // Update existing character
            characterRepository.updateCharacter(
                id = characterId,
                name = name ?: existingCharacter.name,
                type = type ?: existingCharacter.type,
                race = race ?: existingCharacter.race,
                characterClass = characterClass ?: existingCharacter.characterClass,
                subclass = subclass ?: existingCharacter.subclass,
                level = level ?: existingCharacter.level,
                experiencePoints = experiencePoints ?: existingCharacter.experiencePoints,
                abilityScores = abilityScores ?: existingCharacter.abilityScores,
                hitPoints = hitPoints ?: existingCharacter.hitPoints,
                maxHitPoints = maxHitPoints ?: existingCharacter.maxHitPoints,
                temporaryHitPoints = temporaryHitPoints ?: existingCharacter.temporaryHitPoints,
                armorClass = armorClass ?: existingCharacter.armorClass,
                background = background ?: existingCharacter.background,
                alignment = alignment ?: existingCharacter.alignment,
                description = description ?: existingCharacter.description,
                notes = notes ?: existingCharacter.notes,
                items = items ?: existingCharacter.items,
                currency = currency ?: existingCharacter.currency,
                features = features ?: existingCharacter.features,
                updatedAt = now
            )
        } else {
            // Create new character
            characterRepository.insertCharacter(
                id = characterId,
                campaignId = campaignId,
                name = name ?: "",
                type = type ?: CharacterType.PLAYER_CHARACTER,
                race = race ?: "",
                characterClass = characterClass ?: "",
                subclass = subclass ?: "",
                level = level ?: 1,
                experiencePoints = experiencePoints ?: 0,
                abilityScores = abilityScores ?: mapOf(
                    Ability.STRENGTH to 10,
                    Ability.DEXTERITY to 10,
                    Ability.CONSTITUTION to 10,
                    Ability.INTELLIGENCE to 10,
                    Ability.WISDOM to 10,
                    Ability.CHARISMA to 10
                ),
                hitPoints = hitPoints ?: 10,
                maxHitPoints = maxHitPoints ?: 10,
                temporaryHitPoints = temporaryHitPoints ?: 0,
                armorClass = armorClass ?: 10,
                background = background ?: "",
                alignment = alignment ?: Alignment.TRUE_NEUTRAL,
                description = description ?: "",
                notes = notes ?: "",
                items = items ?: emptyList(),
                currency = currency ?: mapOf(
                    "cp" to 0,
                    "sp" to 0,
                    "ep" to 0,
                    "gp" to 0,
                    "pp" to 0
                ),
                features = features ?: emptyList(),
                createdAt = now,
                updatedAt = now
            )
        }

        return characterId
    }

    /**
     * Generates a unique ID for a character
     */
    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }
}
