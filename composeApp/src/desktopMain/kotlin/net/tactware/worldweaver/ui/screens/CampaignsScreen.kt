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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
    onSetActive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            // Header with name and active button
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

                Surface(
                    modifier = Modifier.clickable(onClick = onSetActive),
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.medium,
                            vertical = MaterialTheme.spacing.small
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        if (isActive) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            if (isActive) "Active" else "Set Active",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Campaign details
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                "Setting: ${campaign.setting}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                campaign.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Additional info
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Characters: ${campaign.playerCharacters.size}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Quests: ${campaign.activeQuests.size + campaign.completedQuests.size}",
                    style = MaterialTheme.typography.bodySmall
                )

                // Format date to a readable string
                val createdDate = campaign.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
                Text(
                    "Created: ${createdDate.date}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CampaignsScreen(viewModel: MainViewModel) {
    val campaignService = koinInject<CampaignService>()

    // State for new campaign form
    var showNewCampaignForm by remember { mutableStateOf(false) }
    var campaignName by remember { mutableStateOf("") }
    var campaignDescription by remember { mutableStateOf("") }
    var campaignSetting by remember { mutableStateOf("") }
    var campaignNotes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
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
            Surface(
                modifier = Modifier.clickable {
                    showNewCampaignForm = !showNewCampaignForm
                },
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.medium,
                        vertical = MaterialTheme.spacing.small
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    Icon(
                        imageVector = if (showNewCampaignForm) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (showNewCampaignForm) "Cancel" else "Create New Campaign",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        if (showNewCampaignForm) "Cancel" else "Create New Campaign",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // New Campaign Form
        AnimatedVisibility(visible = showNewCampaignForm) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    Text(
                        "Create New Campaign",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Two-column layout for form fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        // Left column - Basic info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                        ) {
                            // Name Field
                            OutlinedTextField(
                                value = campaignName,
                                onValueChange = { campaignName = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Name") },
                                placeholder = { Text("Enter campaign name") },
                                singleLine = true
                            )

                            // Setting Field
                            OutlinedTextField(
                                value = campaignSetting,
                                onValueChange = { campaignSetting = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Setting") },
                                placeholder = { Text("Enter campaign setting") },
                                singleLine = true
                            )
                        }

                        // Right column - Detailed info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                        ) {
                            // Description Field
                            OutlinedTextField(
                                value = campaignDescription,
                                onValueChange = { campaignDescription = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Description") },
                                placeholder = { Text("Enter campaign description") },
                                minLines = 2,
                                maxLines = 3
                            )

                            // Notes Field
                            OutlinedTextField(
                                value = campaignNotes,
                                onValueChange = { campaignNotes = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Notes") },
                                placeholder = { Text("Enter campaign notes") },
                                minLines = 2,
                                maxLines = 3
                            )
                        }
                    }

                    // Submit Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            modifier = Modifier.clickable {
                                if (campaignName.isNotBlank() && campaignDescription.isNotBlank() && campaignSetting.isNotBlank()) {
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
                            },
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = MaterialTheme.spacing.medium,
                                    vertical = MaterialTheme.spacing.small
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Create Campaign",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "Create Campaign",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Campaign list section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Take remaining space
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
                        }
                    )
                }
            }
        }
    }
}
