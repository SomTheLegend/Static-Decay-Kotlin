package com.stl.lib.staticDecay

/**
 * Represents a weapon item in the game.
 * Weapons can be equipped by the player to deal damage in combat.
 *
 * @param name The name of the weapon.
 * @param description A brief description of the weapon.
 * @property damage The amount of damage this weapon inflicts.
 */
class Weapon(name: String, description: String, val damage: Int) : Item(name, description)
