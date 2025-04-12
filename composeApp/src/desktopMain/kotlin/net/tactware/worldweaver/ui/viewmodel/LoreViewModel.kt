package net.tactware.worldweaver.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import net.tactware.worldweaver.bl.usecase.CreateUpdateLoreUseCase
import net.tactware.worldweaver.bl.usecase.DeleteLoreUseCase
import net.tactware.worldweaver.bl.usecase.GetLoreByIdUseCase
import net.tactware.worldweaver.bl.usecase.GetLoreByCategoryUseCase
import net.tactware.worldweaver.bl.usecase.GetLoreUseCase
import net.tactware.worldweaver.dal.model.lore.Lore
import org.koin.core.annotation.Factory

/**
 * ViewModel for the Lore screen following the MVI pattern.
 * Handles all user interactions and manages the UI state.
 */
@Factory
class LoreViewModel(
    private val getLoreUseCase: GetLoreUseCase,
    private val getLoreByIdUseCase: GetLoreByIdUseCase,
    private val getLoreByCategoryUseCase: GetLoreByCategoryUseCase,
    private val createUpdateLoreUseCase: CreateUpdateLoreUseCase,
    private val deleteLoreUseCase: DeleteLoreUseCase
) : ViewModel() {

    // UI State
    var state by mutableStateOf(LoreScreenState())
        private set

    // Expose lore entries as part of the state
    private val _loreEntries = MutableStateFlow<List<Lore>>(emptyList())
    val loreEntries: StateFlow<List<Lore>> = _loreEntries

    init {
        // Initialize state by loading lore entries
        loadLoreEntries()
    }

    /**
     * Loads all lore entries and updates the state
     */
    private fun loadLoreEntries() {
        _loreEntries.value = getLoreUseCase.execute()
        updateCategories()
    }

    /**
     * Handle user interactions following the MVI pattern.
     * This is the central function for processing all user actions.
     */
    fun onInteraction(action: LoreScreenAction) {
        when (action) {
            is LoreScreenAction.SelectCategory -> {
                state = state.copy(selectedCategory = action.category)
            }
            is LoreScreenAction.ShowNewEntryForm -> {
                state = state.copy(
                    showNewEntryForm = true,
                    editingEntryId = null
                )
            }
            is LoreScreenAction.HideNewEntryForm -> {
                state = state.copy(showNewEntryForm = false)
            }
            is LoreScreenAction.StartEditingEntry -> {
                state = state.copy(
                    editingEntryId = action.entryId,
                    showNewEntryForm = false
                )
            }
            is LoreScreenAction.CancelEditing -> {
                state = state.copy(editingEntryId = null)
            }
            is LoreScreenAction.SelectEntry -> {
                state = state.copy(selectedEntryId = action.entryId)
            }
            is LoreScreenAction.CreateLoreEntry -> {
                createUpdateLoreUseCase.execute(
                    title = action.title,
                    content = action.content,
                    category = action.category,
                    tags = action.tags,
                    relatedEntries = action.relatedEntries
                )
                state = state.copy(showNewEntryForm = false)
                loadLoreEntries()
            }
            is LoreScreenAction.UpdateLoreEntry -> {
                createUpdateLoreUseCase.execute(
                    id = action.id,
                    title = action.title,
                    content = action.content,
                    category = action.category,
                    tags = action.tags,
                    relatedEntries = action.relatedEntries
                )
                state = state.copy(editingEntryId = null)
                loadLoreEntries()
            }
            is LoreScreenAction.DeleteLoreEntry -> {
                deleteLoreUseCase.execute(action.entryId)
                loadLoreEntries()
            }
        }
    }

    /**
     * Updates the categories list based on the current lore entries
     */
    private fun updateCategories() {
        val entries = _loreEntries.value
        val uniqueCategories = entries.map { it.category }.distinct().sorted()
        state = state.copy(categories = listOf("All") + uniqueCategories)
    }

    /**
     * Gets lore entries filtered by the selected category
     */
    fun getFilteredEntries(): List<Lore> {
        return if (state.selectedCategory == "All") {
            _loreEntries.value
        } else {
            getLoreByCategoryUseCase.execute(state.selectedCategory)
        }
    }

    /**
     * Gets a lore entry by ID
     */
    fun getLoreEntryById(id: String): Lore? {
        return getLoreByIdUseCase.execute(id)
    }
}

/**
 * Represents the UI state for the lore screen.
 */
data class LoreScreenState(
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All"),
    val showNewEntryForm: Boolean = false,
    val editingEntryId: String? = null,
    val selectedEntryId: String? = null
)

/**
 * Sealed class representing all possible user interactions with the lore screen.
 */
sealed class LoreScreenAction {
    /**
     * Select a category to filter lore entries.
     */
    data class SelectCategory(val category: String) : LoreScreenAction()

    /**
     * Show the form to create a new lore entry.
     */
    object ShowNewEntryForm : LoreScreenAction()

    /**
     * Hide the form to create a new lore entry.
     */
    object HideNewEntryForm : LoreScreenAction()

    /**
     * Start editing an existing lore entry.
     */
    data class StartEditingEntry(val entryId: String) : LoreScreenAction()

    /**
     * Cancel editing a lore entry.
     */
    object CancelEditing : LoreScreenAction()

    /**
     * Select a lore entry to view its details.
     */
    data class SelectEntry(val entryId: String?) : LoreScreenAction()

    /**
     * Create a new lore entry.
     */
    data class CreateLoreEntry(
        val title: String,
        val content: String,
        val category: String,
        val tags: List<String> = emptyList(),
        val relatedEntries: List<String> = emptyList()
    ) : LoreScreenAction()

    /**
     * Update an existing lore entry.
     */
    data class UpdateLoreEntry(
        val id: String,
        val title: String,
        val content: String,
        val category: String,
        val tags: List<String>,
        val relatedEntries: List<String>
    ) : LoreScreenAction()

    /**
     * Delete a lore entry.
     */
    data class DeleteLoreEntry(val entryId: String) : LoreScreenAction()
}
