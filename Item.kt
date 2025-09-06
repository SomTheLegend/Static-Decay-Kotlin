package com.stl.lib.staticDecay

/**
 * Represents a generic item in the game.
 * This is an open class, intended to be subclassed by more specific item types.
 *
 * @property name The name of the item.
 * @property description A brief description of the item.
 */
open class Item(val name: String, val description: String)
