package com.stl.lib.staticDecay

/**
 * Represents a Whisperer creature in the game.
 * Whisperers are weak creatures that can damage the player's sanity.
 *
 * @param x The initial x-coordinate of the Whisperer.
 * @param y The initial y-coordinate of the Whisperer.
 */
class Whisperer(x: Int, y: Int) : Creature("Whisperer", 20, 5, x, y)
