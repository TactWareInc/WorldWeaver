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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.dal.model.Campaign
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import net.tactware.worldweaver.ui.viewmodel.MainScreenAction
import net.tactware.worldweaver.ui.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
private fun CampaignItem(
    campaign: Campaign,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onEdit: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
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
                    "Quests: ${campaign.activeQuests.size + campaign.completedQuests.size}",
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
fun CampaignsScreen(viewModel: MainViewModel) {
    val campaignService = koinInject<CampaignService>()

    // State for campaign forms
    var showNewCampaignForm by remember { mutableStateOf(false) }
    var campaignName by remember { mutableStateOf("") }
    var campaignDescription by remember { mutableStateOf("") }
    var campaignSetting by remember { mutableStateOf("") }
    var campaignNotes by remember { mutableStateOf("") }

    // State for editing campaigns
    var editingCampaignId by remember { mutableStateOf<String?>(null) }
    var showEditForm by remember { mutableStateOf(false) }

    // State for selected campaign (for list-detail view)
    var selectedCampaign by remember { mutableStateOf<Campaign?>(null) }

    // Function to start editing a campaign
    fun startEditingCampaign(campaign: Campaign) {
        campaignName = campaign.name
        campaignDescription = campaign.description
        campaignSetting = campaign.setting
        campaignNotes = campaign.notes
        editingCampaignId = campaign.id
        showEditForm = true
        showNewCampaignForm = false
    }

    // Function to cancel editing
    fun cancelEditing() {
        editingCampaignId = null
        showEditForm = false
        campaignName = ""
        campaignDescription = ""
        campaignSetting = ""
        campaignNotes = ""
    }

    // Function to save edited campaign
    fun saveEditedCampaign() {
        editingCampaignId?.let { id ->
            viewModel.onInteraction(
                MainScreenAction.UpdateCampaign(
                    id = id,
                    name = campaignName,
                    description = campaignDescription,
                    setting = campaignSetting,
                    notes = campaignNotes
                )
            )
            cancelEditing()
        }
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
                "Campaigns",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                "Manage your campaigns and select the active one.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Display active campaign at the top
            val activeCampaign = campaignService.campaigns.find { it.id == campaignService.activeCampaignId }
            ActiveCampaignDisplay(activeCampaign)
        }

        Divider(modifier = Modifier.fillMaxWidth())

        // Campaign actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Campaigns",
                style = MaterialTheme.typography.titleMedium
            )

            // New Campaign Button
            if (showNewCampaignForm) {
                OutlinedButton(
                    onClick = { showNewCampaignForm = false }
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
                        showNewCampaignForm = true
                        showEditForm = false
                        editingCampaignId = null
                        campaignName = ""
                        campaignDescription = ""
                        campaignSetting = ""
                        campaignNotes = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New Campaign",
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                    )
                    Text("Create New Campaign")
                }
            }
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
                            onValueChange = { campaignName = it },
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
                            onValueChange = { campaignSetting = it },
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
                            onValueChange = { campaignDescription = it },
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
                            onValueChange = { campaignNotes = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Notes") },
                            placeholder = { Text("Enter campaign notes (optional)") },
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
                                    showNewCampaignForm = false
                                    campaignName = ""
                                    campaignDescription = ""
                                    campaignSetting = ""
                                    campaignNotes = ""
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
                                        saveEditedCampaign()
                                    } else {
                                        viewModel.onInteraction(
                                            MainScreenAction.CreateCampaign(
                                                name = campaignName,
                                                description = campaignDescription,
                                                setting = campaignSetting,
                                                notes = campaignNotes
                                            )
                                        )
                                        // Reset form
                                        campaignName = ""
                                        campaignDescription = ""
                                        campaignSetting = ""
                                        campaignNotes = ""
                                        showNewCampaignForm = false
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

        // Campaign list section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp) // Fixed height instead of weight
        ) {
            // Use LazyColumn for better performance with large lists
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                items(campaignService.campaigns) { campaign ->
                    val isActive = campaign.id == campaignService.activeCampaignId
                    CampaignItem(
                        campaign = campaign,
                        isActive = isActive,
                        onSetActive = {
                            viewModel.onInteraction(MainScreenAction.SetActiveCampaign(campaign.id))
                        },
                        onEdit = {
                            startEditingCampaign(campaign)
                        }
                    )
                }
            }
        }
    }
}
