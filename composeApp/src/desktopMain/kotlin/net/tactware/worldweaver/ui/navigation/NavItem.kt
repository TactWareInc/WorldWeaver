package net.tactware.worldweaver.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

// Data class for navigation items
data class NavItem(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)