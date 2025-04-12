package net.tactware.worldweaver.ui.scaffold.state

import androidx.compose.runtime.Stable

/**
 * Sealed interface to represent different states of the navigation panel.
 */
@Stable
sealed interface PanelState {
    /**
     * Expanded state with animation progress.
     */
    @Stable
    data object Expanded : PanelState

    /**
     * Collapsed state with animation progress.
     *
     * @param animationProgress The progress of the collapse animation (0.0 to 1.0).
     */
    @Stable
    data object Collapsed : PanelState
}
