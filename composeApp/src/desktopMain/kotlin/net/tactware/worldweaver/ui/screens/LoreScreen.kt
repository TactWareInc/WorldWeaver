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
import androidx.compose.runtime.key
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import net.tactware.worldweaver.bl.LoreService
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import org.koin.compose.koinInject

// List of preset tags for lore entries
val presetTags = listOf(
    "gods", "creation", "origin", "war", "undead", "heroes", "worship", "divine",
    "magic", "artifact", "location", "character", "monster", "quest", "event",
    "prophecy", "legend", "myth", "ritual", "faction", "kingdom", "city", "dungeon"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LoreEntryForm(
    isEditing: Boolean = false,
    initialTitle: String = "",
    initialContent: String = "",
    initialCategory: String = "",
    initialTags: String = "",
    initialRelatedEntries: List<String> = emptyList(),
    availableLoreEntries: List<LoreService.LoreEntry> = emptyList(),
    currentEntryId: String? = null,
    onSave: (title: String, content: String, category: String, tags: List<String>, relatedEntries: List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    var category by remember { mutableStateOf(initialCategory) }

    // Parse initial tags from comma-separated string
    val initialTagsList = initialTags.split(",").map { it.trim() }.filter { it.isNotBlank() }
    val selectedTags = remember { mutableStateListOf<String>().apply { addAll(initialTagsList) } }

    // Initialize selected related entries
    val selectedRelatedEntries = remember { mutableStateListOf<String>().apply { addAll(initialRelatedEntries) } }

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
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                if (isEditing) "Edit Lore Entry" else "Create New Lore Entry",
                style = MaterialTheme.typography.titleMedium
            )

            // Form fields
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                placeholder = { Text("Enter entry title") },
                singleLine = true,
                supportingText = { 
                    if (title.isBlank()) {
                        Text("Title is required")
                    }
                }
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category") },
                placeholder = { Text("E.g., History, Religion, Mythology") },
                singleLine = true,
                supportingText = { 
                    if (category.isBlank()) {
                        Text("Category is required")
                    }
                }
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Content") },
                placeholder = { Text("Enter lore content") },
                minLines = 4,
                maxLines = 8,
                supportingText = { 
                    if (content.isBlank()) {
                        Text("Content is required")
                    }
                }
            )

            // Tags section with Chips
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Display selected tags
                if (selectedTags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        selectedTags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { selectedTags.remove(tag) },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag"
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                // Divider between selected and available tags
                if (selectedTags.isNotEmpty()) {
                    Divider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                Text(
                    "Available Tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Display preset tags as FilterChips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    presetTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    selectedTags.remove(tag)
                                } else {
                                    selectedTags.add(tag)
                                }
                            },
                            label = { Text(tag) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

            // Related Entries section with Chips
            if (availableLoreEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Related Lore Entries",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    // Display selected related entries
                    if (selectedRelatedEntries.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                        ) {
                            selectedRelatedEntries.forEach { relatedId ->
                                val relatedEntry = availableLoreEntries.find { it.id == relatedId }
                                if (relatedEntry != null) {
                                    // Use key to ensure proper recomposition when selection state changes
                                    key(relatedId) {
                                        InputChip(
                                            selected = true,
                                            onClick = { selectedRelatedEntries.remove(relatedId) },
                                            label = { Text(relatedEntry.title) },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove related entry"
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    }

                    // Divider between selected and available related entries
                    if (selectedRelatedEntries.isNotEmpty()) {
                        Divider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                    }

                    Text(
                        "Available Lore Entries",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    // Display available lore entries as FilterChips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        availableLoreEntries.forEach { entry ->
                            if (entry.id != currentEntryId) { // Don't show the current entry
                                // Use key to ensure proper recomposition when selection state changes
                                key(entry.id) {
                                    val isSelected = selectedRelatedEntries.contains(entry.id)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                selectedRelatedEntries.remove(entry.id)
                                            } else {
                                                selectedRelatedEntries.add(entry.id)
                                            }
                                        },
                                        label = { Text(entry.title) },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected"
                                                )
                                            }
                                        } else null
                                    )
                                }
                            }
                        }
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
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank() && category.isNotBlank()) {
                            onSave(title, content, category, selectedTags.toList(), selectedRelatedEntries.toList())
                        }
                    },
                    enabled = title.isNotBlank() && content.isNotBlank() && category.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = if (isEditing) "Save Changes" else "Create Entry",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text(if (isEditing) "Save Changes" else "Create Entry")
                }
            }
        }
    }
}

@Composable
fun LoreScreen() {
    val campaignService = koinInject<CampaignService>()
    val loreService = koinInject<LoreService>()

    val scrollState = rememberScrollState()

    // State for the selected category tab
    var selectedCategory by remember { mutableStateOf("All") }

    // State for forms and detail view
    var showNewEntryForm by remember { mutableStateOf(false) }
    var editingEntryId by remember { mutableStateOf<String?>(null) }

    // Get unique categories from lore entries
    val categories = remember(loreService.loreEntries) {
        val uniqueCategories = loreService.loreEntries.map { it.category }.distinct().sorted()
        listOf("All") + uniqueCategories
    }

    // Get entries for the selected category
    val filteredEntries = remember(selectedCategory, loreService.loreEntries) {
        if (selectedCategory == "All") {
            loreService.loreEntries
        } else {
            loreService.getLoreEntriesByCategory(selectedCategory)
        }
    }

    // Functions for handling lore entry actions
    fun startEditingEntry(entry: LoreService.LoreEntry) {
        editingEntryId = entry.id
        showNewEntryForm = false
    }

    fun cancelEditing() {
        editingEntryId = null
        showNewEntryForm = false
    }

    fun saveEditedEntry(title: String, content: String, category: String, tags: List<String>, relatedEntries: List<String>) {
        editingEntryId?.let { id ->
            loreService.updateLoreEntry(id, title, content, category, tags, relatedEntries)
            editingEntryId = null
        }
    }

    fun createNewEntry(title: String, content: String, category: String, tags: List<String>, relatedEntries: List<String>) {
        loreService.addLoreEntry(title, content, category, tags, relatedEntries)
        showNewEntryForm = false
    }

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
                "Lore",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Manage your world's lore, history, religions, and other narrative elements.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Display active campaign info
        ActiveCampaignDisplay(campaignService.activeCampaign)

        Divider()

        // Lore actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Lore Entries",
                style = MaterialTheme.typography.titleMedium
            )

            // New Entry Button
            if (showNewEntryForm) {
                OutlinedButton(
                    onClick = { showNewEntryForm = false }
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
                        showNewEntryForm = true
                        editingEntryId = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Lore Entry",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Create New Entry")
                }
            }
        }

        // New Entry Form
        AnimatedVisibility(visible = showNewEntryForm) {
            LoreEntryForm(
                availableLoreEntries = loreService.loreEntries,
                currentEntryId = null,
                onSave = { title, content, category, tags, relatedEntries ->
                    createNewEntry(title, content, category, tags, relatedEntries)
                },
                onCancel = { showNewEntryForm = false }
            )
        }

        // Edit Entry Form
        val editingEntry = editingEntryId?.let { id ->
            loreService.loreEntries.find { it.id == id }
        }

        AnimatedVisibility(visible = editingEntry != null) {
            editingEntry?.let { entry ->
                LoreEntryForm(
                    isEditing = true,
                    initialTitle = entry.title,
                    initialContent = entry.content,
                    initialCategory = entry.category,
                    initialTags = entry.tags.joinToString(", "),
                    initialRelatedEntries = entry.relatedEntries,
                    availableLoreEntries = loreService.loreEntries.filter { it.id != entry.id },
                    currentEntryId = entry.id,
                    onSave = { title, content, category, tags, relatedEntries ->
                        saveEditedEntry(title, content, category, tags, relatedEntries)
                    },
                    onCancel = { cancelEditing() }
                )
            }
        }

        // Category tabs
        if (categories.size > 1) {
            TabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        }

        // Display lore entries for the selected category
        if (filteredEntries.isEmpty()) {
            Text("No lore entries found for this category.")
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                filteredEntries.forEach { entry ->
                    LoreEntryCard(
                        entry = entry,
                        onEdit = { startEditingEntry(entry) },
                        onView = { /* View detail functionality could be added here */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoreEntryCard(
    entry: LoreService.LoreEntry,
    onEdit: () -> Unit = {},
    onView: () -> Unit = {},
    loreService: LoreService = koinInject()
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .clickable { onView() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            // Header with title and buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Edit button
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Lore Entry",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Entry preview
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Additional info
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category display
                FilledTonalButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                ) {
                    Text(entry.category)
                }

                // Tags display
                if (entry.tags.isNotEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(
                            "Tags:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Show up to 3 tags in the card
                            val displayTags = if (entry.tags.size > 3) entry.tags.take(3) + "..." else entry.tags

                            displayTags.forEach { tag ->
                                SuggestionChip(
                                    onClick = { /* No action needed */ },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                }
            }

            // Related entries display
            if (entry.relatedEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column {
                        Text(
                            "Related Lore:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Show up to 2 related entries in the card
                            val displayCount = minOf(2, entry.relatedEntries.size)
                            val displayEntries = entry.relatedEntries.take(displayCount)
                            val remainingCount = entry.relatedEntries.size - displayCount

                            displayEntries.forEach { relatedId ->
                                val relatedEntry = loreService.getLoreEntryById(relatedId)
                                if (relatedEntry != null) {
                                    SuggestionChip(
                                        onClick = { /* View related entry */ },
                                        label = { Text(relatedEntry.title) }
                                    )
                                }
                            }

                            if (remainingCount > 0) {
                                SuggestionChip(
                                    onClick = { /* View all related entries */ },
                                    label = { Text("+$remainingCount more") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
