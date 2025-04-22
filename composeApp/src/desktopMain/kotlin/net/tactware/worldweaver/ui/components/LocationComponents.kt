package net.tactware.worldweaver.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.dal.model.location.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(
    location: Location,
    onEdit: () -> Unit = {},
    onView: () -> Unit = {},
    isSelected: Boolean = false
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .clickable { onView() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.surfaceContainerHigh 
            else 
                MaterialTheme.colorScheme.surfaceContainerLow
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
                    location.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Edit button
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Location preview
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                location.description,
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
                // Language and Dialect display
                Column {
                    Text(
                        "Language: ${location.language}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Dialect: ${location.dialect}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Climate and Terrain display
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Climate: ${location.climate}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Terrain: ${location.terrain}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Party Members indicator
            if (location.hasPartyMembers) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                FilledTonalButton(
                    onClick = {},
                    enabled = false
                ) {
                    Text("Party Members Present")
                }
            }
        }
    }
}

@Composable
fun LocationDetail(
    location: Location,
    onEdit: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    location.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Edit button
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Party Members indicator
            if (location.hasPartyMembers) {
                FilledTonalButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                ) {
                    Text("Party Members Present")
                }
            }

            // Creation and update dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Created: ${location.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (location.updatedAt != location.createdAt) {
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                    Text(
                        "Updated: ${location.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault()).date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Description
            Text(
                "Description",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Text(
                location.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Language and Culture
            Text(
                "Language and Culture",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Language",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.language,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Dialect",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.dialect,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Geography
            Text(
                "Geography",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Climate",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.climate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Terrain",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.terrain,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Only show additional sections if they have content
            if (location.population.isNotBlank() || location.government.isNotBlank() || 
                location.economy.isNotBlank() || location.religion.isNotBlank()) {

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Society
                Text(
                    "Society",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                if (location.population.isNotBlank()) {
                    Text(
                        "Population",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.population,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                if (location.government.isNotBlank()) {
                    Text(
                        "Government",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.government,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                if (location.economy.isNotBlank()) {
                    Text(
                        "Economy",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.economy,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                }

                if (location.religion.isNotBlank()) {
                    Text(
                        "Religion",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        location.religion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // History section
            if (location.history.isNotBlank()) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    "History",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    location.history,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Notes section
            if (location.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    "Notes",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                Text(
                    location.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
