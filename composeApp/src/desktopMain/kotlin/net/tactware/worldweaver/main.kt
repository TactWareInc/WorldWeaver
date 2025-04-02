package net.tactware.worldweaver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import net.tactware.nimbus.appwide.ui.theme.spacing
import net.tactware.worldweaver.ui.NotificationIcon
import net.tactware.worldweaver.ui.navigation.NavItem
import net.tactware.worldweaver.ui.screens.CampaignsScreen
import net.tactware.worldweaver.ui.screens.CharactersScreen
import net.tactware.worldweaver.ui.screens.DashboardScreen
import net.tactware.worldweaver.ui.screens.EncountersScreen
import net.tactware.worldweaver.ui.screens.LocationsScreen
import net.tactware.worldweaver.ui.screens.LoreScreen
import net.tactware.worldweaver.ui.theme.AppTheme
import net.tactware.worldweaver.ui.viewmodel.MainScreenAction
import net.tactware.worldweaver.ui.viewmodel.MainViewModel
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun main() = application {
    startKoin {
        loadKoinModules(WWScanner().module)
    }

    AppTheme{
        Window(
            onCloseRequest = ::exitApplication,
            title = "WorldWeaver",
        ) {
            // Inject the MainViewModel
            val viewModel = koinInject<MainViewModel>()
            val state = viewModel.state

            // Use state from the ViewModel
            val selectedNavItem = state.selectedNavItem
            val causeNavigationToExpand = state.causeNavigationToExpand
            val showNavItemTitles = state.showNavItemTitles
            val expandColumn = state.expandColumn

            LaunchedEffect(causeNavigationToExpand) {
                if (causeNavigationToExpand) {
                    delay(200)
                }
                viewModel.onInteraction(MainScreenAction.UpdateNavTitles(causeNavigationToExpand))
            }

            LaunchedEffect(causeNavigationToExpand) {
                if (!causeNavigationToExpand) {
                    delay(200)
                }
                viewModel.onInteraction(MainScreenAction.UpdateExpandColumn(causeNavigationToExpand))
            }

            val navItems = remember {
                mutableStateListOf(
                    NavItem(
                        title = "Dashboard",
                        icon = Icons.Default.Home,
                        contentDescription = "Dashboard"
                    ),
                    NavItem(
                        title = "Campaigns",
                        icon = Icons.Default.Info,
                        contentDescription = "Campaigns"
                    ),
                    NavItem(
                        title = "Characters",
                        icon = Icons.Default.Person,
                        contentDescription = "Characters"
                    ),
                    NavItem(
                        title = "Locations",
                        icon = Icons.Default.Place,
                        contentDescription = "Locations"
                    ),
                    NavItem(
                        title = "Lore",
                        icon = Icons.Default.Search,
                        contentDescription = "Lore"
                    ),
                    NavItem(
                        title = "Encounters",
                        icon = Icons.Default.Notifications,
                        contentDescription = "Encounters"
                    )
                )
            }

            Scaffold(
                // Modern dashboard doesn't need a bottom bar
            ) { innerPadding ->
                Row(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    // Left sidebar navigation - expandable/collapsible with animation
                    val navWidth by animateDpAsState(
                        targetValue = if (expandColumn) 200.dp else 56.dp,
                        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                        label = "navWidth"
                    )

                    Surface(
                        modifier = Modifier.width(navWidth).fillMaxHeight(),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight().padding(vertical = MaterialTheme.spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.Top)
                        ) {
                            // App logo or icon
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "N",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                            // Navigation items
                            navItems.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = MaterialTheme.spacing.small)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selectedNavItem == index) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable { viewModel.onInteraction(MainScreenAction.NavigateTo(index)) }
                                        .padding(MaterialTheme.spacing.small),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.contentDescription,
                                        tint = if (selectedNavItem == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary.copy(
                                            alpha = 0.7f
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )

                                    // Animate the text visibility
                                    AnimatedVisibility(
                                        visible = showNavItemTitles,
                                        enter = expandHorizontally(animationSpec = tween(durationMillis = 400)),
                                        exit = shrinkHorizontally(animationSpec = tween(durationMillis = 100))
                                    ) {
                                        Row {
                                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                            Text(
                                                item.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (selectedNavItem == index) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary.copy(
                                                    alpha = 0.7f
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    // Main content area
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.small)
                        ) {
                            // Header with title, search, and user profile - smaller and more compact
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.small)
                                    .height(48.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Row for title and toggle button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Toggle button that straddles the navigation bar
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondary) // Different color
                                            .clickable { viewModel.onInteraction(MainScreenAction.ToggleNavigation) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Animate the rotation of the icon
                                        val rotation by animateFloatAsState(
                                            targetValue = if (causeNavigationToExpand) 0f else 180f,
                                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                                            label = "iconRotation"
                                        )

                                        Icon(
                                            Icons.Default.ArrowBack, // Always use ArrowBack, but rotate it
                                            contentDescription = if (causeNavigationToExpand) "Collapse Navigation" else "Expand Navigation",
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.size(20.dp).rotate(rotation)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        navItems[selectedNavItem].title,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                                ) {
                                    // Search box
                                    Surface(
                                        modifier = Modifier.width(240.dp).height(40.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()
                                                .padding(horizontal = MaterialTheme.spacing.small),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                            Text(
                                                "Search...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    }

                                    // Notification icon
                                    NotificationIcon()

                                    // User profile
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "User Profile",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            // Content area based on selected navigation item
                            Box(
                                modifier = Modifier.fillMaxSize().padding(top = MaterialTheme.spacing.medium)
                            ) {
                                when (selectedNavItem) {
                                    0 -> { // Dashboard
                                        DashboardScreen(viewModel)
                                    }
                                    1 -> { // Characters
                                        CharactersScreen()
                                    }
                                    2 -> { // Locations
                                        LocationsScreen()
                                    }
                                    3 -> { // Lore
                                        LoreScreen()
                                    }
                                    4 -> { // Campaigns
                                        CampaignsScreen(viewModel)
                                    }
                                    5 -> { // Encounters
                                        EncountersScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
