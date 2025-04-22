package net.tactware.worldweaver.dal.model

/**
 * Enum representing different game mechanics systems
 */
enum class GameMechanics(val displayName: String) {
    FIFTH_EDITION("5E"),
    PATHFINDER("Pathfinder"),
    CALL_OF_CTHULHU("Call of Cthulhu"),
    STARFINDER("Starfinder"),
    FATE("FATE"),
    SAVAGE_WORLDS("Savage Worlds"),
    CUSTOM("Custom");

    companion object {
        /**
         * Convert a string to a GameMechanics enum value
         * @param value The string value to convert
         * @return The corresponding GameMechanics enum value, or FIFTH_EDITION if the value is not recognized
         */
        fun fromString(value: String): GameMechanics {
            return GameMechanics.entries.find { it.displayName == value } ?: FIFTH_EDITION
        }
    }

    /**
     * Convert the enum to a string
     */
    override fun toString(): String {
        return displayName
    }
}