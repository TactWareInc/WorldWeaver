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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.bl.CharacterService
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import net.tactware.worldweaver.ui.viewmodel.MainScreenAction
import net.tactware.worldweaver.ui.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
private fun CharacterItem(
    character: CharacterService.Character,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .clickable { onSelect() },
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        "Race",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        character.race,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Class",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (character.characterClass.isNotEmpty()) "${character.characterClass} (${character.subclass})" else "N/A",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Level",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (character.level > 0) character.level.toString() else "N/A",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        character.description.ifEmpty { "No description available" },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Additional info
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "HP: ${character.hitPoints}/${character.maxHitPoints}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    "AC: ${character.armorClass}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    "Type: ${character.type.name.replace('_', ' ')}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CharacterDetailView(
    character: CharacterService.Character?,
    onEdit: (CharacterService.Character) -> Unit
) {
    if (character == null) {
        // No character selected
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Select a character to view details",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // Character details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium),
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

                IconButton(onClick = { onEdit(character) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Character",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider()

            // Basic info section
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
                        character.type.name.replace('_', ' '),
                        style = MaterialTheme.typography.bodyLarge
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
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Class and level info
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
                        if (character.characterClass.isNotEmpty()) "${character.characterClass} (${character.subclass})" else "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Level",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (character.level > 0) character.level.toString() else "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Combat stats
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
                        style = MaterialTheme.typography.bodyLarge
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
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Divider()

            // Description
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Description",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    character.description.ifEmpty { "No description available" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Notes
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Notes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    character.notes.ifEmpty { "No notes available" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun CharactersScreen(viewModel: MainViewModel) {
    val campaignService = koinInject<CampaignService>()
    val characterService = koinInject<CharacterService>()

    // State for character forms
    var showNewCharacterForm by remember { mutableStateOf(false) }
    var characterName by remember { mutableStateOf("") }
    var characterRace by remember { mutableStateOf("") }
    var characterClass by remember { mutableStateOf("") }
    var characterSubclass by remember { mutableStateOf("") }
    var characterLevel by remember { mutableStateOf("1") }
    var characterHitPoints by remember { mutableStateOf("10") }
    var characterArmorClass by remember { mutableStateOf("10") }
    var characterDescription by remember { mutableStateOf("") }
    var characterNotes by remember { mutableStateOf("") }
    var selectedCharacterType by remember { mutableStateOf(CharacterService.CharacterType.PLAYER_CHARACTER) }

    // State for filtering characters
    var filterType by remember { mutableStateOf<CharacterService.CharacterType?>(null) }

    // State for editing characters
    var editingCharacterId by remember { mutableStateOf<String?>(null) }
    var showEditForm by remember { mutableStateOf(false) }

    // State for selected character (for list-detail view)
    var selectedCharacter by remember { mutableStateOf<CharacterService.Character?>(null) }

    // Function to start editing a character
    fun startEditingCharacter(character: CharacterService.Character) {
        characterName = character.name
        characterRace = character.race
        characterClass = character.characterClass
        characterSubclass = character.subclass
        characterLevel = character.level.toString()
        characterHitPoints = character.hitPoints.toString()
        characterArmorClass = character.armorClass.toString()
        characterDescription = character.description
        characterNotes = character.notes
        selectedCharacterType = character.type
        editingCharacterId = character.id
        showEditForm = true
        showNewCharacterForm = false
    }

    // Function to cancel editing
    fun cancelEditing() {
        editingCharacterId = null
        showEditForm = false
        characterName = ""
        characterRace = ""
        characterClass = ""
        characterSubclass = ""
        characterLevel = "1"
        characterHitPoints = "10"
        characterArmorClass = "10"
        characterDescription = ""
        characterNotes = ""
        selectedCharacterType = CharacterService.CharacterType.PLAYER_CHARACTER
    }

    // Function to save edited character
    fun saveEditedCharacter() {
        editingCharacterId?.let { id ->
            viewModel.onInteraction(
                MainScreenAction.UpdateCharacter(
                    id = id,
                    name = characterName,
                    type = selectedCharacterType,
                    race = characterRace,
                    characterClass = characterClass,
                    subclass = characterSubclass,
                    level = characterLevel.toIntOrNull(),
                    hitPoints = characterHitPoints.toIntOrNull(),
                    maxHitPoints = characterHitPoints.toIntOrNull(),
                    armorClass = characterArmorClass.toIntOrNull(),
                    description = characterDescription,
                    notes = characterNotes
                )
            )
            cancelEditing()
        }
    }

    // Get filtered characters
    val filteredCharacters = when (filterType) {
        CharacterService.CharacterType.PLAYER_CHARACTER -> characterService.getPlayerCharacters()
        CharacterService.CharacterType.NON_PLAYER_CHARACTER -> characterService.getNonPlayerCharacters()
        CharacterService.CharacterType.MONSTER -> characterService.getMonsters()
        null -> characterService.characters
    }

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        // Header section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Characters",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Manage your characters, NPCs, and monsters.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Display active campaign info
            ActiveCampaignDisplay(campaignService.activeCampaign)
        }

        Divider(modifier = Modifier.fillMaxWidth())

        // Character actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Characters",
                style = MaterialTheme.typography.titleMedium
            )

            // New Character Button
            if (showNewCharacterForm) {
                OutlinedButton(
                    onClick = { showNewCharacterForm = false }
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
                    onClick = { 
                        showNewCharacterForm = true
                        showEditForm = false
                        editingCharacterId = null
                        characterName = ""
                        characterRace = ""
                        characterClass = ""
                        characterSubclass = ""
                        characterLevel = "1"
                        characterHitPoints = "10"
                        characterArmorClass = "10"
                        characterDescription = ""
                        characterNotes = ""
                        selectedCharacterType = CharacterService.CharacterType.PLAYER_CHARACTER
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Character",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Create New Character")
                }
            }
        }

        // Filter buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            OutlinedButton(
                onClick = { filterType = null },
                modifier = Modifier.weight(1f)
            ) {
                Text("All (${characterService.characters.size})")
            }

            OutlinedButton(
                onClick = { filterType = CharacterService.CharacterType.PLAYER_CHARACTER },
                modifier = Modifier.weight(1f)
            ) {
                Text("PCs (${characterService.getPlayerCharacters().size})")
            }

            OutlinedButton(
                onClick = { filterType = CharacterService.CharacterType.NON_PLAYER_CHARACTER },
                modifier = Modifier.weight(1f)
            ) {
                Text("NPCs (${characterService.getNonPlayerCharacters().size})")
            }

            OutlinedButton(
                onClick = { filterType = CharacterService.CharacterType.MONSTER },
                modifier = Modifier.weight(1f)
            ) {
                Text("Monsters (${characterService.getMonsters().size})")
            }
        }

        // Empty placeholder - form will be shown in the list-detail layout

        // List-Detail Layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp), // Increased height for better visibility
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Left side - Character list
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Use LazyColumn for better performance with large lists
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    items(filteredCharacters) { character ->
                        CharacterItem(
                            character = character,
                            isSelected = selectedCharacter?.id == character.id,
                            onSelect = {
                                selectedCharacter = character
                            },
                            onEdit = {
                                startEditingCharacter(character)
                            },
                            onDelete = {
                                viewModel.onInteraction(MainScreenAction.DeleteCharacter(character.id))
                            }
                        )
                    }
                }
            }

            // Right side - Character details or form
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (showNewCharacterForm || showEditForm) {
                    // Show character form
                    ElevatedCard(
                        modifier = Modifier.fillMaxSize().padding(vertical = MaterialTheme.spacing.small),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                        ) {
                            Text(
                                if (showEditForm) "Edit Character" else "Create New Character",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Character type selection
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                            ) {
                                Text(
                                    "Character Type",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                                ) {
                                    CharacterService.CharacterType.values().forEach { type ->
                                        OutlinedButton(
                                            onClick = { selectedCharacterType = type },
                                            modifier = Modifier.weight(1f),
                                            colors = if (selectedCharacterType == type) {
                                                androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            } else {
                                                androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                                            }
                                        ) {
                                            Text(type.name.replace('_', ' '))
                                        }
                                    }
                                }
                            }

                            // Form fields in a scrollable column
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                // Name Field
                                OutlinedTextField(
                                    value = characterName,
                                    onValueChange = { characterName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Name") },
                                    placeholder = { Text("Enter character name") },
                                    singleLine = true,
                                    supportingText = { 
                                        if (characterName.isBlank()) {
                                            Text("Name is required")
                                        }
                                    }
                                )

                                // Race Field
                                OutlinedTextField(
                                    value = characterRace,
                                    onValueChange = { characterRace = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Race") },
                                    placeholder = { Text("Enter character race") },
                                    singleLine = true,
                                    supportingText = { 
                                        if (characterRace.isBlank()) {
                                            Text("Race is required")
                                        }
                                    }
                                )

                                // Class and Subclass Fields
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                                ) {
                                    OutlinedTextField(
                                        value = characterClass,
                                        onValueChange = { characterClass = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Class") },
                                        placeholder = { Text("Enter class") },
                                        singleLine = true,
                                        enabled = selectedCharacterType != CharacterService.CharacterType.MONSTER
                                    )

                                    OutlinedTextField(
                                        value = characterSubclass,
                                        onValueChange = { characterSubclass = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Subclass") },
                                        placeholder = { Text("Enter subclass") },
                                        singleLine = true,
                                        enabled = selectedCharacterType != CharacterService.CharacterType.MONSTER
                                    )
                                }

                                // Level, HP, and AC Fields
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                                ) {
                                    OutlinedTextField(
                                        value = characterLevel,
                                        onValueChange = { 
                                            characterLevel = it.filter { char -> char.isDigit() }
                                        },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Level") },
                                        placeholder = { Text("Enter level") },
                                        singleLine = true,
                                        enabled = selectedCharacterType != CharacterService.CharacterType.MONSTER
                                    )

                                    OutlinedTextField(
                                        value = characterHitPoints,
                                        onValueChange = { 
                                            characterHitPoints = it.filter { char -> char.isDigit() }
                                        },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Hit Points") },
                                        placeholder = { Text("Enter HP") },
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = characterArmorClass,
                                        onValueChange = { 
                                            characterArmorClass = it.filter { char -> char.isDigit() }
                                        },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Armor Class") },
                                        placeholder = { Text("Enter AC") },
                                        singleLine = true
                                    )
                                }

                                // Description Field
                                OutlinedTextField(
                                    value = characterDescription,
                                    onValueChange = { characterDescription = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Description") },
                                    placeholder = { Text("Enter character description") },
                                    minLines = 2,
                                    maxLines = 3
                                )

                                // Notes Field
                                OutlinedTextField(
                                    value = characterNotes,
                                    onValueChange = { characterNotes = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Notes") },
                                    placeholder = { Text("Enter character notes (optional)") },
                                    minLines = 2,
                                    maxLines = 3
                                )
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cancel Button
                                OutlinedButton(
                                    onClick = {
                                        if (showEditForm) {
                                            cancelEditing()
                                        } else {
                                            showNewCharacterForm = false
                                            characterName = ""
                                            characterRace = ""
                                            characterClass = ""
                                            characterSubclass = ""
                                            characterLevel = "1"
                                            characterHitPoints = "10"
                                            characterArmorClass = "10"
                                            characterDescription = ""
                                            characterNotes = ""
                                            selectedCharacterType = CharacterService.CharacterType.PLAYER_CHARACTER
                                        }
                                    },
                                    modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                                ) {
                                    Text("Cancel")
                                }

                                // Submit Button
                                Button(
                                    onClick = {
                                        if (characterName.isNotBlank() && characterRace.isNotBlank()) {
                                            if (showEditForm) {
                                                saveEditedCharacter()
                                            } else {
                                                viewModel.onInteraction(
                                                    MainScreenAction.CreateCharacter(
                                                        name = characterName,
                                                        type = selectedCharacterType,
                                                        race = characterRace,
                                                        characterClass = characterClass,
                                                        subclass = characterSubclass,
                                                        level = characterLevel.toIntOrNull() ?: 1,
                                                        hitPoints = characterHitPoints.toIntOrNull() ?: 10,
                                                        maxHitPoints = characterHitPoints.toIntOrNull() ?: 10,
                                                        armorClass = characterArmorClass.toIntOrNull() ?: 10,
                                                        description = characterDescription,
                                                        notes = characterNotes
                                                    )
                                                )
                                                // Reset form
                                                characterName = ""
                                                characterRace = ""
                                                characterClass = ""
                                                characterSubclass = ""
                                                characterLevel = "1"
                                                characterHitPoints = "10"
                                                characterArmorClass = "10"
                                                characterDescription = ""
                                                characterNotes = ""
                                                showNewCharacterForm = false
                                            }
                                        }
                                    },
                                    enabled = characterName.isNotBlank() && characterRace.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = if (showEditForm) "Save Changes" else "Create Character",
                                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                                    )
                                    Text(if (showEditForm) "Save Changes" else "Create Character")
                                }
                            }
                        }
                    }
                } else {
                    // Show character details
                    CharacterDetailView(
                        character = selectedCharacter,
                        onEdit = { character ->
                            startEditingCharacter(character)
                        }
                    )
                }
            }
        }
    }
}
