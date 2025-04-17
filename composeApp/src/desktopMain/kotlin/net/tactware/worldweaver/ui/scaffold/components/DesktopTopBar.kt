package net.tactware.worldweaver.ui.scaffold.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.tactware.worldweaver.dal.model.campaign.Campaign
import net.tactware.worldweaver.ui.components.ActiveCampaignDisplay

/**
 * Custom implementation of the top bar optimized for desktop applications.
 * This component is designed to be placed at the top of the screen and provide
 * search functionality and global actions.
 *
 * @param title Title or branding element
 * @param activeCampaign The currently active campaign, if any
 * @param search Search component
 * @param actions Actions on the right side
 * @param backgroundColor Background color of the top bar
 * @param contentColor Content color of the top bar
 * @param elevation Elevation of the top bar
 * @param height Height of the top bar
 * @param modifier Modifier for the top bar
 */
@Composable
fun DesktopTopBar(
    title: @Composable () -> Unit = {},
    activeCampaign: Campaign? = null,
    search: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp,
    height: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = elevation,
        modifier = modifier.fillMaxWidth().height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            // Title/branding section
            title()

            // Active campaign section (if available)
            if (activeCampaign != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ActiveCampaignDisplay(activeCampaign)
                }
            }

            // Search section (right-aligned)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(if (activeCampaign != null) 0.5f else 1f),
                horizontalArrangement = Arrangement.End,
                content = { search() }
            )

            // Actions section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}
