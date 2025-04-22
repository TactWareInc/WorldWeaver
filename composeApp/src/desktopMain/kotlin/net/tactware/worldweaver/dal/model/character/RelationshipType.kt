package net.tactware.worldweaver.dal.model.character

/**
 * Enum representing types of character relationships
 */
enum class RelationshipType {
    PARENT,
    CHILD,
    SIBLING,
    SPOUSE,
    ANCESTOR,
    DESCENDANT,
    MENTOR,
    STUDENT,
    ALLY,
    RIVAL,
    ENEMY,
    OTHER;

    companion object {
        fun fromString(value: String): RelationshipType {
            return when (value.uppercase().replace(" ", "_")) {
                "PARENT" -> PARENT
                "CHILD" -> CHILD
                "SIBLING" -> SIBLING
                "SPOUSE" -> SPOUSE
                "ANCESTOR" -> ANCESTOR
                "DESCENDANT" -> DESCENDANT
                "MENTOR" -> MENTOR
                "STUDENT" -> STUDENT
                "ALLY" -> ALLY
                "RIVAL" -> RIVAL
                "ENEMY" -> ENEMY
                "OTHER" -> OTHER
                else -> throw IllegalArgumentException("Unknown relationship type: $value")
            }
        }

        fun toString(relationshipType: RelationshipType): String {
            return when (relationshipType) {
                PARENT -> "Parent"
                CHILD -> "Child"
                SIBLING -> "Sibling"
                SPOUSE -> "Spouse"
                ANCESTOR -> "Ancestor"
                DESCENDANT -> "Descendant"
                MENTOR -> "Mentor"
                STUDENT -> "Student"
                ALLY -> "Ally"
                RIVAL -> "Rival"
                ENEMY -> "Enemy"
                OTHER -> "Other"
            }
        }
    }
}