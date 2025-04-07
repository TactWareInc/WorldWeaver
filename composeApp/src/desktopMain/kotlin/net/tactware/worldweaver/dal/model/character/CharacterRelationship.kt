package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing a relationship between characters
 */
data class CharacterRelationship(
    val relatedCharacterId: String,
    val relationshipType: RelationshipType,
    val description: String = ""
)