package net.tactware.worldweaver.util

/**
 * Singleton object for generating unique IDs across the application.
 * This centralizes ID generation to avoid duplication of code and ensure consistency.
 */
object IdGenerator {
    /**
     * Generates a unique ID for a character
     */
    fun generateCharacterId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for a campaign
     */
    fun generateCampaignId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for a lore entry
     */
    fun generateLoreId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for an encounter
     */
    fun generateEncounterId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for an encounter participant
     */
    fun generateParticipantId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for a location
     */
    fun generateLocationId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a unique ID for a notification
     */
    fun generateNotificationId(): String {
        return generateTimeBasedId()
    }

    /**
     * Generates a time-based unique ID
     * This is the common implementation used by all ID generation methods
     */
    private fun generateTimeBasedId(): String {
        return System.currentTimeMillis().toString()
    }
}