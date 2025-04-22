package net.tactware.worldweaver.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import net.tactware.worldweaver.dal.model.character.Character
import net.tactware.worldweaver.dal.model.character.CharacterType
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import net.tactware.worldweaver.ui.viewmodel.CharacterInteraction
import net.tactware.worldweaver.ui.viewmodel.CharacterViewModel
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.ui.scaffold.components.DesktopAreaScaffold
import net.tactware.worldweaver.ui.scaffold.components.DesktopActionBar
import net.tactware.worldweaver.ui.scaffold.components.DesktopPanel
import net.tactware.worldweaver.ui.scaffold.state.rememberNavigationPanelState
import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject

@Composable
private fun CharacterItem(
    character: Character,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .clickable(onClick = onSelect),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            // Header with name and buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    character.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit button
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Character",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Delete button
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Character",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Character details
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        when(character.type) {
                            CharacterType.PLAYER_CHARACTER -> "Player Character"
                            CharacterType.NON_PLAYER_CHARACTER -> "NPC"
                            CharacterType.MONSTER -> "Monster"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Race",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        character.race,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (character.type == CharacterType.PLAYER_CHARACTER || character.characterClass.isNotBlank()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Class",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${character.characterClass} ${if (character.subclass.isNotBlank()) "(${character.subclass})" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (character.type == CharacterType.PLAYER_CHARACTER || character.level > 0) {
                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(
                            "Level",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            character.level.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationPanelCharacterItem(
    character: Character,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character name with type indicator
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    character.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    when(character.type) {
                        CharacterType.PLAYER_CHARACTER -> "PC - ${character.race} ${character.characterClass}"
                        CharacterType.NON_PLAYER_CHARACTER -> "NPC - ${character.race}"
                        CharacterType.MONSTER -> "Monster - ${character.race}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CharacterDetail(
    character: Character?,
    onEdit: (String) -> Unit
) {
    if (character == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Select a character to view details",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Header with name and edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    character.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = { onEdit(character.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Character",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Edit")
                }
            }

            HorizontalDivider()

            // Basic info section
            Text(
                "Basic Information",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        when(character.type) {
                            CharacterType.PLAYER_CHARACTER -> "Player Character"
                            CharacterType.NON_PLAYER_CHARACTER -> "NPC"
                            CharacterType.MONSTER -> "Monster"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Race",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        character.race,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (character.type == CharacterType.PLAYER_CHARACTER || character.characterClass.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Class",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            character.characterClass,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (character.subclass.isNotBlank()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Subclass",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                character.subclass,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(0.5f)) {
                        Text(
                            "Level",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            character.level.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Divider()

            // Combat stats section
            Text(
                "Combat Stats",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Hit Points",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${character.hitPoints}/${character.maxHitPoints}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Armor Class",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        character.armorClass.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Speed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${character.speed} ft.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Divider()

            // Description section
            Text(
                "Description",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                character.description.ifBlank { "No description available." },
                style = MaterialTheme.typography.bodyMedium
            )

            if (character.notes.isNotBlank()) {
                Divider()

                Text(
                    "Notes",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    character.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CharactersScreen() {
    val viewModel = koinInject<CharacterViewModel>()

    // Get state from the ViewModel
    val state = viewModel.state
    val showNewCharacterForm = state.showNewCharacterForm
    val editingCharacterId = state.editingCharacterId
    val showEditForm = editingCharacterId != null
    val selectedCharacterId = state.selectedCharacterId

    // Get characters from the ViewModel
    val characters = viewModel.characters.collectAsState().value

    // Get active campaign
    val activeCampaign = viewModel.activeCampaign

    // Get selected character
    val selectedCharacter = characters.find { it.id == selectedCharacterId }


    // State for scrolling
    val scrollState = rememberScrollState()
    val navigationPanelState = rememberNavigationPanelState(true)

    DesktopAreaScaffold(
        navigationPanelState = navigationPanelState,
        actionBar = {
            DesktopActionBar(
                expansionAction = {
                    IconButton(onClick = { navigationPanelState.toggle() }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Navigation Panel"
                        )
                    }
                },
                primaryAction = {
                    Button(
                        onClick = { viewModel.onInteraction(CharacterInteraction.ShowNewCharacterForm) },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !showNewCharacterForm && !showEditForm
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create New Character",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("New Character")
                    }
                },
                actions = {
                    // Only show these actions when a character is selected
                    if (selectedCharacter != null) {
                        // Edit button as IconButton
                        IconButton(
                            onClick = { viewModel.onInteraction(CharacterInteraction.StartEditingCharacter(selectedCharacter.id)) },
                            enabled = !showNewCharacterForm && !showEditForm
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Character"
                            )
                        }

                        // Delete button as IconButton
                        IconButton(
                            onClick = { viewModel.onInteraction(CharacterInteraction.DeleteCharacter(selectedCharacter.id)) },
                            enabled = !showNewCharacterForm && !showEditForm
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Character",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        navigationPanel = {
            DesktopPanel(
                header = {
                    Text(
                        "Characters",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                content = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(characters) { character ->
                            val isSelected = selectedCharacterId == character.id
                            NavigationPanelCharacterItem(
                                character = character,
                                isSelected = isSelected,
                                onClick = {
                                    viewModel.onInteraction(CharacterInteraction.SelectCharacter(character.id))
                                    // Close any open forms
                                    if (showNewCharacterForm) {
                                        viewModel.onInteraction(CharacterInteraction.HideNewCharacterForm)
                                    }
                                    if (showEditForm) {
                                        viewModel.onInteraction(CharacterInteraction.CancelEditing)
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                // Header section with selected character
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        if (selectedCharacter != null) "Character: ${selectedCharacter.name}" else "Characters",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    if (selectedCharacter != null) {
                        Text(
                            when(selectedCharacter.type) {
                                CharacterType.PLAYER_CHARACTER -> "Player Character - ${selectedCharacter.race} ${selectedCharacter.characterClass}"
                                CharacterType.NON_PLAYER_CHARACTER -> "NPC - ${selectedCharacter.race}"
                                CharacterType.MONSTER -> "Monster - ${selectedCharacter.race}"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            "Select a character from the navigation panel or create a new one.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                // Form actions row - only show when form is visible
                if (showNewCharacterForm || showEditForm) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (showEditForm) "Edit Character" else "Create New Character",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Cancel button
                        OutlinedButton(
                            onClick = {
                                if (showEditForm) {
                                    viewModel.onInteraction(CharacterInteraction.CancelEditing)
                                } else {
                                    viewModel.onInteraction(CharacterInteraction.HideNewCharacterForm)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                            )
                            Text("Cancel")
                        }
                    }
                } else if (selectedCharacter != null) {
                    // Show character details when a character is selected and no form is visible
                    Text(
                        "Character Details",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Character Form or Details
                if (showNewCharacterForm) {
                    // New Character Form
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                        ) {
                            // Form fields
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                // Name field
                                OutlinedTextField(
                                    value = state.name,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredName(it)) },
                                    label = { Text("Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = state.name.isEmpty()
                                )

                                // Type selection
                                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                    CharacterType.entries.forEach { type ->
                                        SegmentedButton(
                                            selected = viewModel.state.type == type,
                                            onClick = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredType(type)) },
                                            shape = SegmentedButtonDefaults.itemShape(type.ordinal, CharacterType.entries.size)
                                        ) {
                                            Text(
                                                text = when (type) {
                                                    CharacterType.PLAYER_CHARACTER -> "PC"
                                                    CharacterType.NON_PLAYER_CHARACTER -> "NPC"
                                                    CharacterType.MONSTER -> "Monster"
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                // Race field
                                OutlinedTextField(
                                    value = state.race,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredRace(it)) },
                                    label = { Text("Race") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = state.race.isEmpty()
                                )

                                // Class field
                                OutlinedTextField(
                                    value = state.characterClass,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredClass(it)) },
                                    label = { Text("Class") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Subclass field
                                OutlinedTextField(
                                    value = state.subclass,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredSubclass(it)) },
                                    label = { Text("Subclass") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Level field
                                OutlinedTextField(
                                    value = state.level?.toString() ?: "",
                                    onValueChange = { 
                                        val level = it.toIntOrNull()
                                        viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredLevel(level))
                                    },
                                    label = { Text("Level") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Hit Points field
                                OutlinedTextField(
                                    value = state.hitPoints?.toString() ?: "",
                                    onValueChange = { 
                                        val hp = it.toIntOrNull()
                                        viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredHitPoints(hp))
                                    },
                                    label = { Text("Hit Points") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Max Hit Points field
                                OutlinedTextField(
                                    value = state.maxHitPoints?.toString() ?: "",
                                    onValueChange = { 
                                        val maxHp = it.toIntOrNull()
                                        viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredMaxHitPoints(maxHp))
                                    },
                                    label = { Text("Max Hit Points") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Armor Class field
                                OutlinedTextField(
                                    value = state.armorClass?.toString() ?: "",
                                    onValueChange = { 
                                        val ac = it.toIntOrNull()
                                        viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredArmorClass(ac))
                                    },
                                    label = { Text("Armor Class") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Description field
                                OutlinedTextField(
                                    value = state.description,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredDescription(it)) },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )

                                // Notes field
                                OutlinedTextField(
                                    value = state.notes,
                                    onValueChange = { viewModel.onInteraction(CharacterInteraction.DataEntry.EnteredNotes(it)) },
                                    label = { Text("Notes") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("* Required fields", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

                                OutlinedButton(
                                    onClick = { viewModel.onInteraction(CharacterInteraction.HideNewCharacterForm) }
                                ) {
                                    Text("Cancel")
                                }

                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))

                                Button(
                                    onClick = { viewModel.onInteraction(CharacterInteraction.DataEntry.SubmitCreate) },
                                    enabled = state.isFormValid
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Save",
                                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                                    )
                                    Text("Create Character")
                                }
                            }
                        }
                    }
                } else if (selectedCharacter != null && !showEditForm) {
                    // Display character details in a card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        CharacterDetail(
                            character = selectedCharacter,
                            onEdit = { id -> viewModel.onInteraction(CharacterInteraction.StartEditingCharacter(id)) }
                        )
                    }
                } else if (!showNewCharacterForm && !showEditForm && selectedCharacter == null) {
                    // Show a message when no character is selected
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Select a character from the navigation panel or create a new one.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}
