package net.tactware.worldweaver.dal.model.character

/**
 * Data class representing an item in D&D 5E
 */
data class Item(
    val name: String,
    val quantity: Int = 1,
    val weight: Double = 0.0,
    val description: String = "",
    val type: ItemType = ItemType.MISCELLANEOUS,
    val equipped: Boolean = false,
    val properties: List<String> = emptyList()
)