package net.tactware.worldweaver.bl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.annotation.Single

/**
 * Singleton service for handling location information across the application.
 * Manages cities, dungeons, wilderness areas, and other locations in the game world.
 */
@Single
class LocationService {

    /**
     * Enum representing location types
     */
    enum class LocationType {
        CITY,
        TOWN,
        VILLAGE,
        DUNGEON,
        WILDERNESS,
        LANDMARK,
        BUILDING,
        OTHER
    }

    /**
     * Data class representing a location
     */
    data class Location(
        val id: String = Companion.generateId(),
        val name: String,
        val type: LocationType,
        val description: String,
        val notableNPCs: List<String> = emptyList(), // IDs or names of NPCs
        val points_of_interest: List<String> = emptyList(),
        val dangers: List<String> = emptyList(),
        val treasures: List<String> = emptyList(),
        val parentLocationId: String? = null, // For nested locations (e.g., a building within a city)
        val mapCoordinates: Pair<Float, Float>? = null, // X, Y coordinates on a map if applicable
        val notes: String = "",
        val createdAt: Instant = Clock.System.now(),
        val updatedAt: Instant = Clock.System.now()
    )

    // In-memory storage for locations
    private val _locations = mutableStateListOf<Location>()

    // Public read-only access to locations
    val locations: SnapshotStateList<Location> = _locations

    // Initialize with some sample locations
    init {
        // City
        val havenCityId = addLocation(
            name = "Haven City",
            type = LocationType.CITY,
            description = "A bustling metropolis nestled between the mountains and the sea. Known for its grand architecture, diverse population, and thriving trade.",
            notableNPCs = listOf("King Aldric", "Elara Nightshade (Court Mage)", "Captain Thorne (City Guard)"),
            points_of_interest = listOf("Royal Palace", "Grand Market", "Temple District", "Mages' Guild", "Harbor"),
            dangers = listOf("Thieves' Guild operates in the shadows", "Political intrigue and corruption"),
            treasures = listOf("Royal Treasury", "Magical artifacts in the Mages' Guild")
        )

        // Building within the city
        addLocation(
            name = "The Drunken Dragon Tavern",
            type = LocationType.BUILDING,
            description = "A popular tavern in Haven City known for its strong ale, hearty food, and colorful patrons. A favorite gathering spot for adventurers.",
            notableNPCs = listOf("Greta (Bartender)", "Old Man Wilbur (Storyteller)"),
            points_of_interest = listOf("Notice board with job postings", "Secret basement room for private meetings"),
            dangers = listOf("Bar fights", "Thieves targeting drunk patrons"),
            treasures = listOf("Rare wines in the cellar"),
            parentLocationId = havenCityId
        )

        // Dungeon
        addLocation(
            name = "Crypt of the Forgotten King",
            type = LocationType.DUNGEON,
            description = "An ancient burial complex beneath the ruins of Castle Blackmoor. Dark, damp corridors lead to chambers filled with traps and undead guardians.",
            notableNPCs = listOf("Ghost of King Alaric", "Necromancer Zul'than"),
            points_of_interest = listOf("Throne Room", "Treasury", "Ritual Chamber", "Catacombs"),
            dangers = listOf("Undead guardians", "Deadly traps", "Cursed artifacts", "Collapsing passages"),
            treasures = listOf("Crown of the Forgotten King", "Ancient spellbooks", "Royal jewels", "Magical weapons")
        )

        // Wilderness
        addLocation(
            name = "Darkwood Forest",
            type = LocationType.WILDERNESS,
            description = "A vast, ancient forest with towering trees that block out the sun. The deeper one goes, the stranger and more dangerous it becomes.",
            notableNPCs = listOf("Elven Ranger Patrols", "Dryad Council", "Grommash the Troll"),
            points_of_interest = listOf("Heart of the Forest (ancient tree)", "Fey Crossing", "Abandoned Elven Ruins", "Crystal Clear Lake"),
            dangers = listOf("Territorial wolf packs", "Giant spiders", "Fey tricksters", "Lost travelers who never find their way out"),
            treasures = listOf("Rare herbs and plants", "Enchanted wood for crafting", "Forgotten elven artifacts")
        )
    }

    /**
     * Adds a new location and returns its ID
     */
    fun addLocation(
        name: String,
        type: LocationType,
        description: String,
        notableNPCs: List<String> = emptyList(),
        points_of_interest: List<String> = emptyList(),
        dangers: List<String> = emptyList(),
        treasures: List<String> = emptyList(),
        parentLocationId: String? = null,
        mapCoordinates: Pair<Float, Float>? = null,
        notes: String = ""
    ): String {
        val id = Companion.generateId()
        _locations.add(
            Location(
                id = id,
                name = name,
                type = type,
                description = description,
                notableNPCs = notableNPCs,
                points_of_interest = points_of_interest,
                dangers = dangers,
                treasures = treasures,
                parentLocationId = parentLocationId,
                mapCoordinates = mapCoordinates,
                notes = notes
            )
        )
        return id
    }

    /**
     * Updates an existing location
     */
    fun updateLocation(
        id: String,
        name: String? = null,
        type: LocationType? = null,
        description: String? = null,
        notableNPCs: List<String>? = null,
        points_of_interest: List<String>? = null,
        dangers: List<String>? = null,
        treasures: List<String>? = null,
        parentLocationId: String? = null,
        mapCoordinates: Pair<Float, Float>? = null,
        notes: String? = null
    ) {
        val index = _locations.indexOfFirst { it.id == id }
        if (index != -1) {
            val location = _locations[index]
            _locations[index] = location.copy(
                name = name ?: location.name,
                type = type ?: location.type,
                description = description ?: location.description,
                notableNPCs = notableNPCs ?: location.notableNPCs,
                points_of_interest = points_of_interest ?: location.points_of_interest,
                dangers = dangers ?: location.dangers,
                treasures = treasures ?: location.treasures,
                parentLocationId = parentLocationId ?: location.parentLocationId,
                mapCoordinates = mapCoordinates ?: location.mapCoordinates,
                notes = notes ?: location.notes,
                updatedAt = Clock.System.now()
            )
        }
    }

    /**
     * Removes a location
     */
    fun removeLocation(id: String) {
        _locations.removeIf { it.id == id }
    }

    /**
     * Gets locations by type
     */
    fun getLocationsByType(type: LocationType): List<Location> {
        return _locations.filter { it.type == type }
    }

    /**
     * Gets child locations of a parent location
     */
    fun getChildLocations(parentId: String): List<Location> {
        return _locations.filter { it.parentLocationId == parentId }
    }

    /**
     * Gets top-level locations (those without a parent)
     */
    fun getTopLevelLocations(): List<Location> {
        return _locations.filter { it.parentLocationId == null }
    }

    /**
     * Searches locations by name or description
     */
    fun searchLocations(query: String): List<Location> {
        val lowercaseQuery = query.lowercase()
        return _locations.filter { 
            it.name.lowercase().contains(lowercaseQuery) || 
            it.description.lowercase().contains(lowercaseQuery) ||
            it.notes.lowercase().contains(lowercaseQuery)
        }
    }

    companion object {
        /**
         * Generates a unique ID for a location
         */
        internal fun generateId(): String {
            return System.currentTimeMillis().toString()
        }
    }
}
