package com.stl.lib.staticDecay

/**
 * Represents an Anomaly creature in the game.
 * Anomalies are powerful, stationary creatures with high health but no direct attack.
 *
 * @param x The initial x-coordinate of the Anomaly.
 * @param y The initial y-coordinate of the Anomaly.
 */
class Anomaly(x: Int, y: Int) : Creature("Anomaly", 200, 0, x, y)
