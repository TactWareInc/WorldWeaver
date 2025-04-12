package net.tactware.worldweaver.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import net.tactware.worldweaver.dal.model.campaign.Campaign
import net.tactware.worldweaver.dal.model.GameMechanics
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import net.tactware.worldweaver.ui.scaffold.components.DesktopActionBar
import net.tactware.worldweaver.ui.scaffold.components.DesktopAreaScaffold
import net.tactware.worldweaver.ui.scaffold.components.DesktopPanel
import net.tactware.worldweaver.ui.scaffold.state.rememberNavigationPanelState
import net.tactware.worldweaver.ui.viewmodel.CampaignScreenAction
import net.tactware.worldweaver.ui.viewmodel.CampaignViewModel
import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject

@Composable
private fun CampaignItem(
    campaign: Campaign,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
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
                    campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit button
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Campaign",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Active/Set Active button
                    if (isActive) {
                        FilledTonalButton(
                            onClick = {},
                            enabled = false
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                            )
                            Text("Active")
                        }
                    } else {
                        Button(
                            onClick = onSetActive
                        ) {
                            Text("Set Active")
                        }
                    }
                }
            }

            // Campaign details
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Setting",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        campaign.setting,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        "Description",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        campaign.description,
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
                    "Characters: ${campaign.playerCharacters.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    "Location: ${if (campaign.activeQuests.isNotEmpty()) campaign.activeQuests.joinToString(", ") else "None"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Format date to a readable string
                val createdDate = campaign.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    "Created: ${createdDate.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NavigationPanelCampaignItem(
    campaign: Campaign,
    isActive: Boolean,
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
            isActive -> MaterialTheme.colorScheme.primaryContainer
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
            // Campaign name with active indicator
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    campaign.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    campaign.setting,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Active indicator
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active Campaign",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CampaignsScreen() {
    val viewModel = koinInject<CampaignViewModel>()

    // Get state from the ViewModel
    val state = viewModel.state
    val showNewCampaignForm = state.showNewCampaignForm
    val editingCampaignId = state.editingCampaignId
    val showEditForm = editingCampaignId != null

    // Get form fields from the ViewModel state
    val campaignName = state.campaignName
    val campaignDescription = state.campaignDescription
    val campaignSetting = state.campaignSetting
    val campaignNotes = state.campaignNotes
    val campaignMechanics = state.campaignMechanics

    // Get campaigns from the ViewModel
    val campaigns = viewModel.campaigns.collectAsState().value

    // Get active campaign
    val activeCampaign = viewModel.getActiveCampaign()

    // State for selected campaign
    var selectedCampaignId by remember { mutableStateOf<String?>(null) }
    val selectedCampaign = campaigns.find { it.id == selectedCampaignId }

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
                        onClick = { viewModel.onInteraction(CampaignScreenAction.ShowNewCampaignForm) },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "Create New Campaign",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("New Campaign")
                    }
                },
                actions = {
                    // Only show these actions when a campaign is selected
                    if (selectedCampaign != null) {
                        // Edit button as IconButton
                        IconButton(
                            onClick = { viewModel.onInteraction(CampaignScreenAction.StartEditingCampaign(selectedCampaign.id)) },
                            enabled = !showNewCampaignForm && !showEditForm
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Campaign"
                            )
                        }

                        // Set Active button as IconButton (only if not already active)
                        if (selectedCampaign.id != activeCampaign?.id) {
                            IconButton(
                                onClick = { viewModel.onInteraction(CampaignScreenAction.SetActiveCampaign(selectedCampaign.id)) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Set Active"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {},
                                enabled = false
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            )
        },
        navigationPanel = {
            DesktopPanel(
                header = {
                    Text(
                        "Campaigns",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                },
                content = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(campaigns) { campaign ->
                            val isActive = activeCampaign?.id == campaign.id
                            val isSelected = selectedCampaignId == campaign.id
                            NavigationPanelCampaignItem(
                                campaign = campaign,
                                isActive = isActive,
                                isSelected = isSelected,
                                onClick = { 
                                    selectedCampaignId = campaign.id
                                    // Close any open forms
                                    if (showNewCampaignForm) {
                                        viewModel.onInteraction(CampaignScreenAction.HideNewCampaignForm)
                                    }
                                    if (showEditForm) {
                                        viewModel.onInteraction(CampaignScreenAction.CancelEditing)
                                    }
                                }
                            )
                        }
                    }
                },
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
        // Header section with selected campaign or active campaign
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                if (selectedCampaign != null) "Campaign: ${selectedCampaign.name}" else "Campaigns",
                style = MaterialTheme.typography.headlineMedium
            )

            if (selectedCampaign != null) {
                Text(
                    "Setting: ${selectedCampaign.setting}",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    "Select a campaign from the navigation panel or create a new one.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Display active campaign at the top if no campaign is selected
            if (selectedCampaign == null) {
                ActiveCampaignDisplay(activeCampaign)
            }
        }

        Divider(modifier = Modifier.fillMaxWidth())

        // Form actions row - only show when form is visible
        if (showNewCampaignForm || showEditForm) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (showEditForm) "Edit Campaign" else "Create New Campaign",
                    style = MaterialTheme.typography.titleMedium
                )

                // Cancel button
                OutlinedButton(
                    onClick = { 
                        if (showEditForm) {
                            viewModel.onInteraction(CampaignScreenAction.CancelEditing)
                        } else {
                            viewModel.onInteraction(CampaignScreenAction.HideNewCampaignForm)
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
        } else if (selectedCampaign != null) {
            // Show campaign details when a campaign is selected and no form is visible
            Text(
                "Campaign Details",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Campaign Forms
        AnimatedVisibility(visible = showNewCampaignForm || showEditForm) {
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
                    Text(
                        if (showEditForm) "Edit Campaign" else "Create New Campaign",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Form fields in a more responsive layout
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        // Name Field
                        OutlinedTextField(
                            value = campaignName,
                            onValueChange = { viewModel.onInteraction(CampaignScreenAction.UpdateCampaignName(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Name") },
                            placeholder = { Text("Enter campaign name") },
                            singleLine = true,
                            supportingText = { 
                                if (campaignName.isBlank()) {
                                    Text("Name is required")
                                }
                            }
                        )

                        // Setting Field
                        OutlinedTextField(
                            value = campaignSetting,
                            onValueChange = { viewModel.onInteraction(CampaignScreenAction.UpdateCampaignSetting(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Setting") },
                            placeholder = { Text("Enter campaign setting") },
                            singleLine = true,
                            supportingText = { 
                                if (campaignSetting.isBlank()) {
                                    Text("Setting is required")
                                }
                            }
                        )

                        // Description Field
                        OutlinedTextField(
                            value = campaignDescription,
                            onValueChange = { viewModel.onInteraction(CampaignScreenAction.UpdateCampaignDescription(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Description") },
                            placeholder = { Text("Enter campaign description") },
                            minLines = 2,
                            maxLines = 3,
                            supportingText = { 
                                if (campaignDescription.isBlank()) {
                                    Text("Description is required")
                                }
                            }
                        )

                        // Notes Field
                        OutlinedTextField(
                            value = campaignNotes,
                            onValueChange = { viewModel.onInteraction(CampaignScreenAction.UpdateCampaignNotes(it)) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Notes") },
                            placeholder = { Text("Enter campaign notes (optional)") },
                            minLines = 2,
                            maxLines = 3
                        )

                        // Mechanics Field
                        OutlinedTextField(
                            value = campaignMechanics.displayName,
                            onValueChange = { mechanicsName ->
                                viewModel.onInteraction(CampaignScreenAction.UpdateCampaignMechanics(GameMechanics.fromString(mechanicsName)))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mechanics") },
                            placeholder = { Text("Enter game mechanics (e.g., 5E, Pathfinder)") },
                            singleLine = true
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
                                    viewModel.onInteraction(CampaignScreenAction.CancelEditing)
                                } else {
                                    viewModel.onInteraction(CampaignScreenAction.HideNewCampaignForm)
                                }
                            },
                            modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                        ) {
                            Text("Cancel")
                        }

                        // Submit Button
                        Button(
                            onClick = {
                                if (campaignName.isNotBlank() && campaignDescription.isNotBlank() && campaignSetting.isNotBlank()) {
                                    if (showEditForm) {
                                        editingCampaignId?.let { id ->
                                            viewModel.onInteraction(
                                                CampaignScreenAction.UpdateCampaign(
                                                    id = id,
                                                    name = campaignName,
                                                    description = campaignDescription,
                                                    setting = campaignSetting,
                                                    notes = campaignNotes,
                                                    mechanics = campaignMechanics
                                                )
                                            )
                                        }
                                    } else {
                                        viewModel.onInteraction(
                                            CampaignScreenAction.CreateCampaign(
                                                name = campaignName,
                                                description = campaignDescription,
                                                setting = campaignSetting,
                                                notes = campaignNotes,
                                                mechanics = campaignMechanics
                                            )
                                        )
                                    }
                                }
                            },
                            enabled = campaignName.isNotBlank() && campaignDescription.isNotBlank() && campaignSetting.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = if (showEditForm) "Save Changes" else "Create Campaign",
                                modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                            )
                            Text(if (showEditForm) "Save Changes" else "Create Campaign")
                        }
                    }
                }
            }
        }

        // Display campaign details when a campaign is selected and no form is visible
        if (selectedCampaign != null && !showNewCampaignForm && !showEditForm) {
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
                    // Campaign details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Setting",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                selectedCampaign.setting,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Column(modifier = Modifier.weight(2f)) {
                            Text(
                                "Description",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                selectedCampaign.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Notes
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Notes",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            selectedCampaign.notes ?: "No notes",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Additional info
                    Divider()
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Characters: ${selectedCampaign.playerCharacters.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            "Game Mechanics: ${selectedCampaign.mechanics.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Format date to a readable string
                        val createdDate = selectedCampaign.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                        Text(
                            "Created: ${createdDate.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (!showNewCampaignForm && !showEditForm && selectedCampaign == null) {
            // Show a message when no campaign is selected
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Select a campaign from the navigation panel or create a new one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
        }
    )
}
