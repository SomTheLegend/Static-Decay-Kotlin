package com.stl.lib.staticDecay

/**
 * Represents a generic creature in the game.
 *
 * @property name The name of the creature.
 * @property hp The current health points of the creature.
 * @property attack The attack power of the creature.
 * @property x The current x-coordinate of the creature.
 * @property y The current y-coordinate of the creature.
 */
open class Creature(val name: String, var hp: Int, val attack: Int, var x: Int, var y: Int) {

    /**
     * Reduces the creature's health points by the specified amount.
     * Ensures that HP does not fall below 0.
     *
     * @param damage The amount of damage to inflict.
     */
    open fun takeDamage(damage: Int) {
        hp -= damage
        if (hp < 0) {
            hp = 0
        }
    }
}
