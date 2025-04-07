package net.tactware.worldweaver.dal.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.db.DatabaseProvider
import net.tactware.worldweaver.dal.model.character.Ability
import net.tactware.worldweaver.dal.model.character.Alignment
import net.tactware.worldweaver.dal.model.character.CharacterType
import net.tactware.worldweaver.dal.model.character.Feature
import net.tactware.worldweaver.dal.model.character.Item
import org.koin.core.annotation.Single
import migrations.net.tactware.worldweaver.Character as DbCharacter

/**
 * Implementation of the CharacterRepository interface.
 * Handles database operations for Character entities.
 */
@Single([CharacterRepository::class])
class CharacterRepositoryImpl(
    private val databaseProvider: DatabaseProvider
) : CharacterRepository {

    override fun getAllCharacters(): List<Character> {
        return try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.getAllCharacters().executeAsList().map { 
                mapDbCharacterToDomain(it)
            }
        } catch (e: Exception) {
            println("Error getting all characters: ${e.message}")
            emptyList()
        }
    }

    override fun getCharacterById(id: String): Flow<Character?> {
        return flow {
            try {
                val db = databaseProvider.getDatabase()
                val character = db.characterQueries.getCharacterById(id).executeAsOneOrNull()?.let {
                    mapDbCharacterToDomain(it)
                }
                emit(character)
            } catch (e: Exception) {
                println("Error getting character by ID: ${e.message}")
                emit(null)
            }
        }
    }

    override fun insertCharacter(
        id: String,
        campaignId : String,
        name: String,
        type: CharacterType,
        race: String,
        characterClass: String,
        subclass: String,
        level: Int,
        experiencePoints: Int,
        abilityScores: Map<Ability, Int>,
        hitPoints: Int,
        maxHitPoints: Int,
        temporaryHitPoints: Int,
        armorClass: Int,
        background: String,
        alignment: Alignment,
        description: String,
        notes: String,
        items: List<Item>,
        currency: Map<String, Int>,
        features: List<Feature>,
        createdAt: Instant,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.insertCharacter(
                id = id,
                campaignId = campaignId,
                name = name,
                type = type.name,
                race = race,
                characterClass = characterClass,
                subclass = subclass,
                level = level.toLong(),
                experiencePoints = experiencePoints.toLong(),
                abilityScores = Json.encodeToString(abilityScores),
                hitPoints = hitPoints.toLong(),
                maxHitPoints = maxHitPoints.toLong(),
                temporaryHitPoints = temporaryHitPoints.toLong(),
                armorClass = armorClass.toLong(),
                background = background,
                alignment = alignment.name,
                description = description,
                notes = notes,
                items = Json.encodeToString(items),
                currency = Json.encodeToString(currency),
                features = Json.encodeToString(features),
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()
            )
        } catch (e: Exception) {
            println("Error inserting character: ${e.message}")
        }
    }

    override fun updateCharacter(
        id: String,
        name: String,
        type: CharacterType,
        race: String,
        characterClass: String,
        subclass: String,
        level: Int,
        experiencePoints: Int,
        abilityScores: Map<Ability, Int>,
        hitPoints: Int,
        maxHitPoints: Int,
        temporaryHitPoints: Int,
        armorClass: Int,
        background: String,
        alignment: Alignment,
        description: String,
        notes: String,
        items: List<Item>,
        currency: Map<String, Int>,
        features: List<Feature>,
        updatedAt: Instant
    ) {
        try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.updateCharacter(
                name = name,
                type = type.name,
                race = race,
                characterClass = characterClass,
                subclass = subclass,
                level = level.toLong(),
                experiencePoints = experiencePoints.toLong(),
                abilityScores = Json.encodeToString(abilityScores),
                hitPoints = hitPoints.toLong(),
                maxHitPoints = maxHitPoints.toLong(),
                temporaryHitPoints = temporaryHitPoints.toLong(),
                armorClass = armorClass.toLong(),
                background = background,
                alignment = alignment.name,
                description = description,
                notes = notes,
                items = Json.encodeToString(items),
                currency = Json.encodeToString(currency),
                features = Json.encodeToString(features),
                updatedAt = updatedAt.toString(),
                id = id
            )
        } catch (e: Exception) {
            println("Error updating character: ${e.message}")
        }
    }

    override fun deleteCharacter(id: String) {
        try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.deleteCharacter(id)
        } catch (e: Exception) {
            println("Error deleting character: ${e.message}")
        }
    }

    override fun searchCharacters(query: String): List<Character> {
        return try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.searchCharacters(query).executeAsList().map {
                mapDbCharacterToDomain(it)
            }
        } catch (e: Exception) {
            println("Error searching characters: ${e.message}")
            emptyList()
        }
    }

    override fun searchCharactersLike(query: String): List<Character> {
        return try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.searchCharactersLike(query).executeAsList().map {
                mapDbCharacterToDomain(it)
            }
        } catch (e: Exception) {
            println("Error searching characters with LIKE: ${e.message}")
            emptyList()
        }
    }


    override fun getCharactersByType(type: CharacterType): List<Character> {
        return try {
            val db = databaseProvider.getDatabase()
            db.characterQueries.getCharactersByType(type.name).executeAsList().map {
                mapDbCharacterToDomain(it)
            }
        } catch (e: Exception) {
            println("Error getting characters by type: ${e.message}")
            emptyList()
        }
    }

    private fun mapDbCharacterToDomain(dbCharacter: DbCharacter): Character {
        return Character(
            id = dbCharacter.id,
            name = dbCharacter.name,
            type = CharacterType.valueOf(dbCharacter.type),
            race = dbCharacter.race,
            characterClass = dbCharacter.characterClass,
            subclass = dbCharacter.subclass,
            level = dbCharacter.level.toInt(),
            experiencePoints = dbCharacter.experiencePoints.toInt(),
            abilityScores = Json.decodeFromString(dbCharacter.abilityScores),
            hitPoints = dbCharacter.hitPoints.toInt(),
            maxHitPoints = dbCharacter.maxHitPoints.toInt(),
            temporaryHitPoints = dbCharacter.temporaryHitPoints.toInt(),
            armorClass = dbCharacter.armorClass.toInt(),
            background = dbCharacter.background,
            alignment = Alignment.valueOf(dbCharacter.alignment),
            description = dbCharacter.description,
            notes = dbCharacter.notes,
            items = Json.decodeFromString(dbCharacter.items),
            currency = Json.decodeFromString(dbCharacter.currency),
            features = Json.decodeFromString(dbCharacter.features),
            createdAt = Instant.parse(dbCharacter.createdAt),
            updatedAt = Instant.parse(dbCharacter.updatedAt)
        )
    }
}
