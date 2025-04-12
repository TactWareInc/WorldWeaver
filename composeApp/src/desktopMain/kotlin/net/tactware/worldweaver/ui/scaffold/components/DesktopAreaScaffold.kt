package net.tactware.worldweaver.ui.scaffold.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.tactware.worldweaver.ui.scaffold.state.NavigationPanelState
import net.tactware.worldweaver.ui.scaffold.state.OptionalPanelState
import net.tactware.worldweaver.ui.scaffold.state.PanelState
import net.tactware.worldweaver.ui.scaffold.state.rememberNavigationPanelState

@Composable
fun DesktopAreaScaffold(
    actionBar: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    navigationPanel: @Composable () -> Unit = {},
    navigationPanelState: NavigationPanelState = rememberNavigationPanelState(),
    navigationPanelWidth : Dp = 240.dp,
    optionalPanel: (@Composable () -> Unit)? = null,
    optionalPanelState: OptionalPanelState? = null,
    optionalPanelWidth : Dp = 240.dp,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        // Action bar area
        actionBar()

        Row {    // Navigation panel (expandable)
            WidthExpandingCard(navigationPanelState.isExpanded, navigationPanelWidth, Modifier.padding(4.dp)) {
                Box(modifier = Modifier.width(navigationPanelWidth).fillMaxHeight()) {
                    navigationPanel()
                }
            }

            // Main content area
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Card(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    content()
                }
            }

            if (optionalPanel != null && optionalPanelState != null) {

                WidthExpandingCard (optionalPanelState.isExpanded, width = optionalPanelWidth, Modifier.padding(4.dp)) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        optionalPanel()
                    }
                }
            }
        }
    }
}