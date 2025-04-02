package net.tactware.worldweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.bl.CampaignService
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay
import org.koin.compose.koinInject

@Composable
fun EncountersScreen() {
    val campaignService = koinInject<CampaignService>()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Text(
            "Encounters",
            style = MaterialTheme.typography.headlineMedium
        )

        // Display active campaign info
        ActiveCampaignDisplay(campaignService.activeCampaign)

        // Encounters content will be added in a separate edit
        Text("Encounters content coming soon...")
    }
}