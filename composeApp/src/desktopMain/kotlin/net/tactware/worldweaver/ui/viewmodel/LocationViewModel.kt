package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.tactware.worldweaver.bl.usecase.CreateUpdateLocationUseCase
import net.tactware.worldweaver.bl.usecase.DeleteLocationUseCase
import net.tactware.worldweaver.bl.usecase.GetLocationsByCampaignUseCase
import net.tactware.worldweaver.bl.usecase.GetLocationsByPartyMembersUseCase
import net.tactware.worldweaver.bl.usecase.GetLocationsUseCase
import net.tactware.worldweaver.dal.model.location.Location
import org.koin.core.annotation.Factory

/**
 * ViewModel for the Location screen following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 */
@Factory
class LocationViewModel(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val getLocationsByCampaignUseCase: GetLocationsByCampaignUseCase,
    private val getLocationsByPartyMembersUseCase: GetLocationsByPartyMembersUseCase,
    private val createUpdateLocationUseCase: CreateUpdateLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    // UI State
    var state by mutableStateOf(LocationScreenState())
        private set

    // Expose location entries as part of the state
    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations

    init {
        // Initialize state by loading locations
        loadLocations()
    }

    /**
     * Loads all locations and updates the state
     */
    private fun loadLocations() {
        _locations.value = getLocationsUseCase.execute()
    }

    /**
     * Handle user interactions following the MVI pattern.
     * This is the central function for processing all user actions.
     */
    fun onInteraction(action: LocationInteraction) {
        when (action) {
            is LocationInteraction.SelectLocation -> {
                state = state.copy(selectedLocationId = action.locationId)
            }
            is LocationInteraction.ShowNewLocationForm -> {
                state = state.copy(
                    showNewLocationForm = true,
                    editingLocationId = null,
                    currentLocation = Location(
                        name = "",
                        description = "",
                        language = "",
                        dialect = "",
                        climate = "",
                        terrain = "",
                        population = "",
                        government = "",
                        economy = "",
                        religion = "",
                        landmarks = emptyList(),
                        history = "",
                        notes = "",
                        hasPartyMembers = false
                    )
                )
            }
            is LocationInteraction.HideNewLocationForm -> {
                state = state.copy(showNewLocationForm = false)
            }
            is LocationInteraction.StartEditingLocation -> {
                val location = _locations.value.find { it.id == action.locationId }
                if (location != null) {
                    state = state.copy(
                        editingLocationId = action.locationId,
                        showNewLocationForm = false,
                        currentLocation = location
                    )
                }
            }
            is LocationInteraction.CancelEditing -> {
                state = state.copy(editingLocationId = null)
            }
            is LocationInteraction.FilterByPartyMembers -> {
                state = state.copy(filterByPartyMembers = action.hasPartyMembers)
                if (action.hasPartyMembers) {
                    _locations.value = getLocationsByPartyMembersUseCase.execute(true)
                } else {
                    loadLocations()
                }
            }
            is LocationInteraction.CreateLocation -> {
                val location = state.currentLocation
                if (location != null && canSaveLocation(location)) {
                    createUpdateLocationUseCase.execute(
                        name = location.name,
                        description = location.description,
                        language = location.language,
                        dialect = location.dialect,
                        climate = location.climate,
                        terrain = location.terrain,
                        population = location.population,
                        government = location.government,
                        economy = location.economy,
                        religion = location.religion,
                        landmarks = location.landmarks,
                        history = location.history,
                        notes = location.notes,
                        hasPartyMembers = location.hasPartyMembers
                    )
                    state = state.copy(showNewLocationForm = false, currentLocation = null)
                    loadLocations()
                }
            }
            is LocationInteraction.UpdateLocation -> {
                val location = state.currentLocation
                if (location != null && canSaveLocation(location)) {
                    createUpdateLocationUseCase.execute(
                        id = action.locationId,
                        name = location.name,
                        description = location.description,
                        language = location.language,
                        dialect = location.dialect,
                        climate = location.climate,
                        terrain = location.terrain,
                        population = location.population,
                        government = location.government,
                        economy = location.economy,
                        religion = location.religion,
                        landmarks = location.landmarks,
                        history = location.history,
                        notes = location.notes,
                        hasPartyMembers = location.hasPartyMembers
                    )
                    state = state.copy(editingLocationId = null, currentLocation = null)
                    loadLocations()
                }
            }
            is LocationInteraction.DeleteLocation -> {
                deleteLocationUseCase.execute(action.locationId)
                state = state.copy(selectedLocationId = null, editingLocationId = null)
                loadLocations()
            }
            is LocationInteraction.DataEntry -> {
                // Handle data entry interactions
                val currentLocation = state.currentLocation ?: return
                val updatedLocation = when (action) {
                    is LocationInteraction.DataEntry.UpdateName -> 
                        currentLocation.copy(name = action.name)
                    is LocationInteraction.DataEntry.UpdateDescription -> 
                        currentLocation.copy(description = action.description)
                    is LocationInteraction.DataEntry.UpdateLanguage -> 
                        currentLocation.copy(language = action.language)
                    is LocationInteraction.DataEntry.UpdateDialect -> 
                        currentLocation.copy(dialect = action.dialect)
                    is LocationInteraction.DataEntry.UpdateClimate -> 
                        currentLocation.copy(climate = action.climate)
                    is LocationInteraction.DataEntry.UpdateTerrain -> 
                        currentLocation.copy(terrain = action.terrain)
                    is LocationInteraction.DataEntry.UpdatePopulation -> 
                        currentLocation.copy(population = action.population)
                    is LocationInteraction.DataEntry.UpdateGovernment -> 
                        currentLocation.copy(government = action.government)
                    is LocationInteraction.DataEntry.UpdateEconomy -> 
                        currentLocation.copy(economy = action.economy)
                    is LocationInteraction.DataEntry.UpdateReligion -> 
                        currentLocation.copy(religion = action.religion)
                    is LocationInteraction.DataEntry.UpdateLandmarks -> 
                        currentLocation.copy(landmarks = action.landmarks)
                    is LocationInteraction.DataEntry.UpdateHistory -> 
                        currentLocation.copy(history = action.history)
                    is LocationInteraction.DataEntry.UpdateNotes -> 
                        currentLocation.copy(notes = action.notes)
                    is LocationInteraction.DataEntry.UpdateHasPartyMembers -> 
                        currentLocation.copy(hasPartyMembers = action.hasPartyMembers)
                }
                
                state = state.copy(
                    currentLocation = updatedLocation,
                    canSave = canSaveLocation(updatedLocation)
                )
            }
        }
    }

    /**
     * Checks if a location can be saved (has all required fields)
     */
    private fun canSaveLocation(location: Location): Boolean {
        return location.name.isNotBlank() &&
                location.description.isNotBlank() &&
                location.language.isNotBlank() &&
                location.dialect.isNotBlank() &&
                location.climate.isNotBlank() &&
                location.terrain.isNotBlank()
    }
}

/**
 * Represents the UI state for the location screen.
 */
data class LocationScreenState(
    val selectedLocationId: String? = null,
    val showNewLocationForm: Boolean = false,
    val editingLocationId: String? = null,
    val filterByPartyMembers: Boolean = false,
    val currentLocation: Location? = null,
    val canSave: Boolean = false
)

/**
 * Sealed class representing all possible user interactions with the location screen.
 */
sealed class LocationInteraction {
    /**
     * Select a location to view its details.
     */
    data class SelectLocation(val locationId: String?) : LocationInteraction()

    /**
     * Show the form to create a new location.
     */
    object ShowNewLocationForm : LocationInteraction()

    /**
     * Hide the form to create a new location.
     */
    object HideNewLocationForm : LocationInteraction()

    /**
     * Start editing an existing location.
     */
    data class StartEditingLocation(val locationId: String) : LocationInteraction()

    /**
     * Cancel editing a location.
     */
    object CancelEditing : LocationInteraction()

    /**
     * Filter locations by whether they have party members.
     */
    data class FilterByPartyMembers(val hasPartyMembers: Boolean) : LocationInteraction()

    /**
     * Create a new location.
     */
    object CreateLocation : LocationInteraction()

    /**
     * Update an existing location.
     */
    data class UpdateLocation(val locationId: String) : LocationInteraction()

    /**
     * Delete a location.
     */
    data class DeleteLocation(val locationId: String) : LocationInteraction()

    /**
     * Sealed class for data entry interactions.
     * These interactions modify different components of the location.
     */
    sealed class DataEntry : LocationInteraction() {
        data class UpdateName(val name: String) : DataEntry()
        data class UpdateDescription(val description: String) : DataEntry()
        data class UpdateLanguage(val language: String) : DataEntry()
        data class UpdateDialect(val dialect: String) : DataEntry()
        data class UpdateClimate(val climate: String) : DataEntry()
        data class UpdateTerrain(val terrain: String) : DataEntry()
        data class UpdatePopulation(val population: String) : DataEntry()
        data class UpdateGovernment(val government: String) : DataEntry()
        data class UpdateEconomy(val economy: String) : DataEntry()
        data class UpdateReligion(val religion: String) : DataEntry()
        data class UpdateLandmarks(val landmarks: List<String>) : DataEntry()
        data class UpdateHistory(val history: String) : DataEntry()
        data class UpdateNotes(val notes: String) : DataEntry()
        data class UpdateHasPartyMembers(val hasPartyMembers: Boolean) : DataEntry()
    }
}