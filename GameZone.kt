package com.stl.lib.staticDecay

/**
 * Represents a zone in the game, including its layout, creatures, and interactable objects.
 *
 * @property name The name of the game zone.
 * @property width The width of the game zone.
 * @property height The height of the game zone.
 * @property layout A 2D array representing the tile layout of the zone.
 * @property creatures A mutable list of creatures present in this zone.
 * @property interactables A map of coordinates to descriptions of interactable objects.
 */
class GameZone(
    val name: String,
    val width: Int,
    val height: Int,
    val layout: Array<CharArray>,
    val creatures: MutableList<Creature>,
    val interactables: MutableMap<Pair<Int, Int>, String>
) {

    private val visited = Array(height) { BooleanArray(width) }

    /**
     * Gets the tile character at the specified coordinates.
     * Returns '#' if the coordinates are out of bounds.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The character representing the tile at the given coordinates.
     */
    fun getTile(x: Int, y: Int): Char {
        if (x in 0 until width && y in 0 until height) {
            return layout[y][x]
        }
        return '#' // Default to wall if out of bounds
    }

    /**
     * Checks if the tile at the specified coordinates has been visited.
     * Returns false if the coordinates are out of bounds.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the tile has been visited, false otherwise.
     */
    fun isVisited(x: Int, y: Int): Boolean {
        if (x in 0 until width && y in 0 until height) {
            return visited[y][x]
        }
        return false
    }

    /**
     * Marks the tile at the specified coordinates as visited.
     * Does nothing if the coordinates are out of bounds.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    fun markVisited(x: Int, y: Int) {
        if (x in 0 until width && y in 0 until height) {
            visited[y][x] = true
        }
    }

    /**
     * Removes an interactable object from the specified coordinates and updates the layout.
     * Does nothing if the coordinates are out of bounds.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    fun removeInteractable(x: Int, y: Int) {
        if (x in 0 until width && y in 0 until height) {
            interactables.remove(x to y)
            // Ensure the tile is something passable after removing an interactable
            if (layout[y][x] != '.') { 
                layout[y][x] = '.'
            }
        }
    }
}
