package net.tactware.worldweaver.dal.model.location

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import net.tactware.worldweaver.util.IdGenerator

/**
 * Data class representing a location in a campaign world
 */
data class Location(
    val id: String = IdGenerator.generateLocationId(),
    val campaignId: String = "",
    val name: String,
    val description: String,
    val language: String,
    val dialect: String,
    val climate: String,
    val terrain: String,
    val population: String, // Could be a number or a description like "sparse", "dense", etc.
    val government: String,
    val economy: String,
    val religion: String,
    val landmarks: List<String> = emptyList(), // Notable landmarks in the location
    val history: String,
    val notes: String,
    val hasPartyMembers: Boolean = false, // Indicates if members of the party are at this location
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
