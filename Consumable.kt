package com.stl.lib.staticDecay

/**
 * Represents a consumable item in the game.
 * Consumable items have an effect when used by the player.
 *
 * @param name The name of the consumable item.
 * @param description A brief description of the consumable item.
 * @property effect A lambda function that defines the action to be performed when the item is consumed.
 *                  It takes a [Player] object as input and returns a [String] message describing the effect.
 */
class Consumable(name: String, description: String, val effect: (Player) -> String)
    : Item(name, description)
