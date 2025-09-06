package com.stl.lib.staticDecay

/**
 * Represents a crafting recipe in the game.
 *
 * @property ingredients A map where keys are item names (String) required for crafting,
 *                      and values are the quantities (Int) of each item needed.
 * @property result The [Item] that is produced by this crafting recipe.
 */
data class CraftingRecipe(val ingredients: Map<String, Int>, val result: Item)
