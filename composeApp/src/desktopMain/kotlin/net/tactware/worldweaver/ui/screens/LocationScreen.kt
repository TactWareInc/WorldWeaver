package net.tactware.worldweaver.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.location.Location
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import net.tactware.worldweaver.ui.components.LocationCard
import net.tactware.worldweaver.ui.components.LocationDetail
import net.tactware.worldweaver.ui.viewmodel.LocationInteraction
import net.tactware.worldweaver.ui.viewmodel.LocationViewModel
import org.koin.compose.koinInject

// List of preset climate types
val presetClimates = listOf(
    "tropical", "dry", "temperate", "continental", "polar", "alpine", "mediterranean", 
    "humid", "arid", "semi-arid", "coastal", "oceanic", "desert", "rainforest", "tundra"
)

// List of preset terrain types
val presetTerrains = listOf(
    "mountains", "hills", "plains", "forest", "jungle", "desert", "tundra", "swamp", 
    "marsh", "coast", "island", "underwater", "cave", "valley", "plateau", "canyon", "volcano"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LocationEntryForm(
    isEditing: Boolean = false,
    location: Location,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDataEntry: (LocationInteraction.DataEntry) -> Unit,
    canSave: Boolean
) {
    val scrollState = rememberScrollState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                if (isEditing) "Edit Location" else "Create New Location",
                style = MaterialTheme.typography.titleMedium
            )

            // Basic Information Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    "Basic Information",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = location.name,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    placeholder = { Text("Enter location name") },
                    singleLine = true,
                    supportingText = { 
                        if (location.name.isBlank()) {
                            Text("Name is required")
                        }
                    }
                )

                OutlinedTextField(
                    value = location.description,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateDescription(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    placeholder = { Text("Enter location description") },
                    minLines = 3,
                    maxLines = 5,
                    supportingText = { 
                        if (location.description.isBlank()) {
                            Text("Description is required")
                        }
                    }
                )
            }

            Divider()

            // Language and Culture Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    "Language and Culture",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = location.language,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateLanguage(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Language") },
                    placeholder = { Text("Primary language spoken") },
                    singleLine = true,
                    supportingText = { 
                        if (location.language.isBlank()) {
                            Text("Language is required")
                        }
                    }
                )

                OutlinedTextField(
                    value = location.dialect,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateDialect(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Dialect") },
                    placeholder = { Text("Regional dialect or variation") },
                    singleLine = true,
                    supportingText = { 
                        if (location.dialect.isBlank()) {
                            Text("Dialect is required")
                        }
                    }
                )
            }

            Divider()

            // Geography Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    "Geography",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Climate with presets
                OutlinedTextField(
                    value = location.climate,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateClimate(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Climate") },
                    placeholder = { Text("Type of climate") },
                    singleLine = true,
                    supportingText = { 
                        if (location.climate.isBlank()) {
                            Text("Climate is required")
                        }
                    }
                )

                Text(
                    "Suggested Climates",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    presetClimates.forEach { climate ->
                        FilterChip(
                            selected = location.climate == climate,
                            onClick = { onDataEntry(LocationInteraction.DataEntry.UpdateClimate(climate)) },
                            label = { Text(climate) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Terrain with presets
                OutlinedTextField(
                    value = location.terrain,
                    onValueChange = { onDataEntry(LocationInteraction.DataEntry.UpdateTerrain(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Terrain") },
                    placeholder = { Text("Type of terrain") },
                    singleLine = true,
                    supportingText = { 
                        if (location.terrain.isBlank()) {
                            Text("Terrain is required")
                        }
                    }
                )

                Text(
                    "Suggested Terrains",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    presetTerrains.forEach { terrain ->
                        FilterChip(
                            selected = location.terrain == terrain,
                            onClick = { onDataEntry(LocationInteraction.DataEntry.UpdateTerrain(terrain)) },
                            label = { Text(terrain) }
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Button
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                ) {
                    Text("Cancel")
                }

                // Submit Button
                Button(
                    onClick = onSave,
                    enabled = canSave
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = if (isEditing) "Save Changes" else "Create Location",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text(if (isEditing) "Save Changes" else "Create Location")
                }
            }
        }
    }
}

@Composable
fun LocationScreen() {
    val campaignService = koinInject<CampaignService>()
    val viewModel = koinInject<LocationViewModel>()

    // Get state from the ViewModel
    val state = viewModel.state
    val showNewLocationForm = state.showNewLocationForm
    val editingLocationId = state.editingLocationId
    val selectedLocationId = state.selectedLocationId
    val currentLocation = state.currentLocation
    val canSave = state.canSave

    // Get locations from the ViewModel
    val locations by viewModel.locations.collectAsState()

    // Get the selected location
    val selectedLocation = selectedLocationId?.let { id ->
        locations.find { it.id == id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        // Header section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Locations",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Manage your world's locations, regions, cities, and other geographical elements.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Display active campaign info
        ActiveCampaignDisplay(campaignService.activeCampaign)

        Divider()

        // Location actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Locations",
                style = MaterialTheme.typography.titleMedium
            )

            // New Location Button
            if (showNewLocationForm) {
                OutlinedButton(
                    onClick = { viewModel.onInteraction(LocationInteraction.HideNewLocationForm) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Cancel")
                }
            } else {
                Button(
                    onClick = { viewModel.onInteraction(LocationInteraction.ShowNewLocationForm) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Location",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Create New Location")
                }
            }
        }

        // New Location Form
        AnimatedVisibility(visible = showNewLocationForm) {
            currentLocation?.let { location ->
                LocationEntryForm(
                    location = location,
                    onSave = { viewModel.onInteraction(LocationInteraction.CreateLocation) },
                    onCancel = { viewModel.onInteraction(LocationInteraction.HideNewLocationForm) },
                    onDataEntry = { viewModel.onInteraction(it) },
                    canSave = canSave
                )
            }
        }

        // Edit Location Form
        val editingLocation = editingLocationId?.let { id ->
            locations.find { it.id == id }
        }

        AnimatedVisibility(visible = editingLocation != null && currentLocation != null) {
            currentLocation?.let { location ->
                LocationEntryForm(
                    isEditing = true,
                    location = location,
                    onSave = { 
                        editingLocationId?.let { id ->
                            viewModel.onInteraction(LocationInteraction.UpdateLocation(id))
                        }
                    },
                    onCancel = { viewModel.onInteraction(LocationInteraction.CancelEditing) },
                    onDataEntry = { viewModel.onInteraction(it) },
                    canSave = canSave
                )
            }
        }

        // List-Detail View
        if (locations.isEmpty()) {
            Text("No locations found.")
        } else {
            // Only show the list-detail view if we're not showing forms
            AnimatedVisibility(visible = !showNewLocationForm && editingLocation == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    // List view (left side)
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        // Filter by Party Members
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Show only locations with party members",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Switch(
                                checked = state.filterByPartyMembers,
                                onCheckedChange = { viewModel.onInteraction(LocationInteraction.FilterByPartyMembers(it)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                        locations.forEach { location ->
                            LocationCard(
                                location = location,
                                isSelected = location.id == selectedLocationId,
                                onEdit = { viewModel.onInteraction(LocationInteraction.StartEditingLocation(location.id)) },
                                onView = { viewModel.onInteraction(LocationInteraction.SelectLocation(location.id)) }
                            )
                        }
                    }

                    // Detail view (right side)
                    Column(
                        modifier = Modifier.weight(0.6f)
                    ) {
                        selectedLocation?.let { location ->
                            LocationDetail(
                                location = location,
                                onEdit = { viewModel.onInteraction(LocationInteraction.StartEditingLocation(location.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
