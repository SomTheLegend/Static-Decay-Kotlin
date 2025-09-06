package com.stl.lib.staticDecay

/**
 * Represents a resource item in the game.
 * Resources are typically used as ingredients in crafting recipes.
 *
 * @param name The name of the resource.
 * @param description A brief description of the resource.
 */
class Resource(name: String, description: String) : Item(name, description)
