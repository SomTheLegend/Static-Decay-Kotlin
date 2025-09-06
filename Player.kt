package com.stl.lib.staticDecay

/**
 * Represents the player in the game.
 *
 * @property hp The current health points of the player. Defaults to 100.
 * @property hunger The current hunger level of the player. Defaults to 100.
 * @property sanity The current sanity level of the player. Defaults to 100.
 * @property x The current x-coordinate of the player.
 * @property y The current y-coordinate of the player.
 * @property inventory A map of items in the player's inventory and their counts.
 * @property equippedWeapon The currently equipped weapon. Can be null.
 */
data class Player(
    var hp: Int = 100,
    var hunger: Int = 100,
    var sanity: Int = 100,
    var x: Int,
    var y: Int,
    val inventory: MutableMap<Item, Int> = mutableMapOf(),
    var equippedWeapon: Weapon? = null
) {

    /**
     * Reduces the player's health points by the specified amount.
     * HP cannot go below 0.
     *
     * @param damage The amount of damage to take.
     */
    fun takeDamage(damage: Int) {
        hp -= damage
        if (hp <= 0) {
            hp = 0
        }
    }

    /**
     * Increases the player's health points by the specified amount.
     * HP cannot exceed 100.
     *
     * @param amount The amount of HP to restore.
     */
    fun heal(amount: Int) {
        hp += amount
        if (hp > 100) {
            hp = 100
        }
    }

    /**
     * Decreases the player's hunger level by the specified amount.
     * Hunger cannot go below 0. If hunger reaches 0, a message is printed.
     *
     * @param amount The amount of hunger to lose.
     */
    fun loseHunger(amount: Int) {
        hunger -= amount
        if (hunger < 0) {
            // Consider moving this side effect (println) to the Game class for better separation
            println("You are starving! You lose 5 HP")
            hp -= 5 // Example penalty for starving
            if (hp < 0) hp = 0
            hunger = 0
        }
    }

    /**
     * Increases the player's hunger level by the specified amount.
     * Hunger cannot exceed 100.
     *
     * @param amount The amount of hunger to restore.
     */
    fun eat(amount: Int) {
        hunger += amount
        if (hunger > 100) {
            hunger = 100
        }
    }

    /**
     * Decreases the player's sanity level by the specified amount.
     * Sanity cannot go below 0.
     *
     * @param amount The amount of sanity to lose.
     */
    fun loseSanity(amount: Int) {
        sanity -= amount
        if (sanity < 0) {
            sanity = 0
        }
    }

    /**
     * Increases the player's sanity level by the specified amount.
     * Sanity cannot exceed 100.
     *
     * @param amount The amount of sanity to restore.
     */
    fun gainSanity(amount: Int) {
        sanity += amount
        if (sanity > 100) {
            sanity = 100
        }
    }

    /**
     * Adds an item to the player's inventory.
     *
     * @param item The item to add.
     * @param count The number of items to add. Defaults to 1.
     */
    fun addItem(item: Item, count: Int = 1) {
        inventory[item] = inventory.getOrDefault(item, 0) + count
    }

    /**
     * Removes an item from the player's inventory.
     *
     * @param item The item to remove.
     * @param count The number of items to remove. Defaults to 1.
     * @return True if the item was successfully removed, false otherwise (e.g., not enough items).
     */
    fun removeItem(item: Item, count: Int = 1) : Boolean {
        val currentCount = inventory.getOrDefault(item, 0)
        if (currentCount >= count) {
            inventory[item] = currentCount - count
            if (inventory[item] == 0) {
                inventory.remove(item)
            }
            return true
        }
        return false
    }

    /**
     * Checks if the player has a specific item in their inventory.
     *
     * @param itemName The name of the item to check for (case-insensitive).
     * @return True if the player has the item, false otherwise.
     */
    fun hasItem(itemName: String) : Boolean {
        return inventory.keys.any { it.name.equals(itemName, ignoreCase = true) }
    }
}
