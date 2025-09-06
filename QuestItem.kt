package com.stl.lib.staticDecay

/**
 * Represents a quest item in the game.
 * Quest items are typically required to progress through certain parts of the game.
 *
 * @param name The name of the quest item.
 * @param description A brief description of the quest item.
 */
class QuestItem(name: String, description: String) : Item(name, description)
