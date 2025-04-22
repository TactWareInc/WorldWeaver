package net.tactware.worldweaver.dal.model.character

/**
 * Enum representing character alignments in D&D 5E
 */
enum class Alignment {
    LAWFUL_GOOD,
    NEUTRAL_GOOD,
    CHAOTIC_GOOD,
    LAWFUL_NEUTRAL,
    TRUE_NEUTRAL,
    CHAOTIC_NEUTRAL,
    LAWFUL_EVIL,
    NEUTRAL_EVIL,
    CHAOTIC_EVIL,
    UNALIGNED;

    companion object {
        fun fromString(value: String): Alignment {
            return when (value.uppercase().replace(" ", "_")) {
                "LAWFUL_GOOD", "LG" -> LAWFUL_GOOD
                "NEUTRAL_GOOD", "NG" -> NEUTRAL_GOOD
                "CHAOTIC_GOOD", "CG" -> CHAOTIC_GOOD
                "LAWFUL_NEUTRAL", "LN" -> LAWFUL_NEUTRAL
                "TRUE_NEUTRAL", "NEUTRAL", "N" -> TRUE_NEUTRAL
                "CHAOTIC_NEUTRAL", "CN" -> CHAOTIC_NEUTRAL
                "LAWFUL_EVIL", "LE" -> LAWFUL_EVIL
                "NEUTRAL_EVIL", "NE" -> NEUTRAL_EVIL
                "CHAOTIC_EVIL", "CE" -> CHAOTIC_EVIL
                "UNALIGNED" -> UNALIGNED
                else -> throw IllegalArgumentException("Unknown alignment: $value")
            }
        }

        fun toString(alignment: Alignment): String {
            return when (alignment) {
                LAWFUL_GOOD -> "Lawful Good"
                NEUTRAL_GOOD -> "Neutral Good"
                CHAOTIC_GOOD -> "Chaotic Good"
                LAWFUL_NEUTRAL -> "Lawful Neutral"
                TRUE_NEUTRAL -> "True Neutral"
                CHAOTIC_NEUTRAL -> "Chaotic Neutral"
                LAWFUL_EVIL -> "Lawful Evil"
                NEUTRAL_EVIL -> "Neutral Evil"
                CHAOTIC_EVIL -> "Chaotic Evil"
                UNALIGNED -> "Unaligned"
            }
        }
    }
}