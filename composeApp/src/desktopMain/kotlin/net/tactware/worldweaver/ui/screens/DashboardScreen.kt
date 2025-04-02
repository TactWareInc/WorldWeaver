package net.tactware.worldweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.ui.components.TorchesLayout
import net.tactware.worldweaver.ui.viewmodel.MainScreenAction
import net.tactware.worldweaver.ui.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Torches in the corners above the welcome text
        TorchesLayout(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            flameHeight = 120
        )

        Text(
            "Welcome to WorldWeaver",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            "Your Dungeon Master's companion for creating and managing your D&D worlds.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        // Quick stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            // Characters stat
            Surface(
                modifier = Modifier.weight(1f).height(100.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Characters",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "3 Total",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // Locations stat
            Surface(
                modifier = Modifier.weight(1f).height(100.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Locations",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "4 Total",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // Campaigns stat
            val campaignService = koinInject<CampaignService>()
            Surface(
                modifier = Modifier.weight(1f).height(100.dp).clickable {
                    // Navigate to Campaigns section
                    viewModel.onInteraction(MainScreenAction.NavigateTo(4))
                },
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.medium),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Campaigns",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${campaignService.campaigns.size} Total, ",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            if (campaignService.activeCampaignId != null) "1 Active" else "0 Active",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Text(
                        "Click to manage campaigns",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
