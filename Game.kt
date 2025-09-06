package com.stl.lib.staticDecay

import java.util.Locale
import kotlin.random.Random

/**
 * Manages the overall game state, game loop, and interactions within the Static Decay game.
 * This class initializes the game world, handles player input, creature AI, combat,
 * and game progression.
 */
class Game {

    private val player = Player(x = 1, y = 1)
    private lateinit var currentZone: GameZone
    private val items = mutableMapOf<String, Item>()
    private val recipes = mutableListOf<CraftingRecipe>()
    private val messageLog = mutableListOf<String>()
    private var gameOver = false

    /**
     * Initializes the game by setting up items, recipes, the starting zone,
     * revealing the initial area, and logging the starting message.
     */
    init {
        initializeItems()
        initializeRecipes()
        currentZone = createSubwayZone() // Start in the subway
        revealInitialArea()
        log("You awaken in a cold, damp subway tunnel. The silence is deafening.")
    }

    /**
     * Populates the [items] map with all available items in the game.
     * This includes weapons, consumables, resources, and quest items.
     */
    private fun initializeItems() {
        // WEAPONS
        items["Makeshift Shiv"] = Weapon("Makeshift Shiv", "A crude piece of metal.", 15)
        items["9mm Pistol"] = Weapon("9mm Pistol", "A standard handgun. Reliable.", 35)

        // CONSUMABLES
        items["Bandage"] = Consumable("Bandage", "Stops bleeding and restores a little health.") { p ->
            p.heal(20)
            "You apply the bandage. It stings, but you feel much better. (+20 HP)"
        }
        items["Med-kit"] = Consumable("Med-kit", "A proper medical kit. Restores significant health.") { p ->
            p.heal(75)
            "You apply the med-kit. The relief is immediate. (+75 HP)"
        }
        items["Canned Food"] = Consumable("Canned Food", "Suspicious, but edible. Might help with hunger.") { p ->
            p.eat(40)
            "It doesn't taste good, but it's food. (+40 HG)"
        }
        items["Thrown Bottle"] = Consumable("Thrown Bottle", "Creates a noise to distract enemies.") { _ ->
            // Actual distraction logic would be implemented elsewhere, e.g., in enemy AI.
            "You get ready to throw the bottle."
        }

        // RESOURCES
        items["Scrap Metal"] = Resource("Scrap Metal", "Could be useful for crafting.")
        items["Dirty Rags"] = Resource("Dirty Rags", "Filthy, but might have a use.")
        items["Chemicals"] = Resource("Chemicals", "A volatile mix of unknown substances.")
        items["Herbs"] = Resource("Herbs", "Some strange-looking plants.")
        items["Wood"] = Resource("Wood", "A splintered piece of wood.")
        items["Ammo"] = Resource("Ammo", "Rounds for a firearm.") // Not currently used for pistol
        items["Batteries"] = Resource("Batteries", "Power for your flashlight.") // Flashlight not implemented

        // QUEST ITEMS
        items["Crowbar"] = QuestItem("Crowbar", "Useful for prying things open.")
        items["Security Keycard"] = QuestItem("Security Keycard", "Opens electronically locked doors.")
    }

    /**
     * Populates the [recipes] list with all available crafting recipes in the game.
     * Recipes define what items can be crafted from other items.
     */
    private fun initializeRecipes() {
        recipes.add(CraftingRecipe(mapOf("Dirty Rags" to 1, "Chemicals" to 1), items["Bandage"]!!))
        recipes.add(CraftingRecipe(mapOf("Scrap Metal" to 1, "Wood" to 1), items["Makeshift Shiv"]!!))
        recipes.add(CraftingRecipe(mapOf("Herbs" to 1, "Chemicals" to 1), items["Med-kit"]!!))
    }

    /**
     * Creates and configures the "Abandoned Subway" game zone.
     * Sets the player's starting position for this zone.
     * @return The configured [GameZone] object for the Abandoned Subway.
     */
    private fun createSubwayZone(): GameZone {
        val layout = arrayOf(
            "####################",
            "#P........#.........#", // P indicates a potential player start, but player is set below
            "#.C.M.##.D.#.M.....#",
            "#.....##...#.......#",
            "######?#####.......#",
            "#............M.....#",
            "####################"
        )
        val creatures = mutableListOf<Creature>(
            Shambler(5, 2), Shambler(15, 2), Shambler(15, 5)
        )
        val interactables = mutableMapOf(
            (2 to 2) to "C:A rusted locker.",
            (9 to 2) to "D:A door, jammed shut. It leads to the surface.",
            (6 to 4) to "?:A blood-stained journal lies on the ground."
        )
        player.x = 1 // Set player starting position
        player.y = 1
        return GameZone("Abandoned Subway", 20, 7,
            layout.map { it.toCharArray() }.toTypedArray(), creatures, interactables)
    }

    /**
     * Creates and configures the "City Center" game zone.
     * Sets the player's starting position for this zone.
     * @return The configured [GameZone] object for the City Center.
     */
    private fun createCityCenterZone(): GameZone {
        val layout = arrayOf(
            "#######################",
            "#P........#...........#",
            "#..M..C...#...M.......#",
            "#.........D...........#",
            "#.........#....?......#",
            "#..C......#...........#",
            "#######################"
        )
        val creatures = mutableListOf<Creature>(
            Shambler(4, 2), Shambler(14, 2)
        )
        val interactables = mutableMapOf(
            (6 to 2) to "C:A ransacked storefront.",
            (4 to 5) to "C:An overturned police car.",
            (10 to 3) to "D:A maintenance hatch, sealed tight.",
            (17 to 4) to "?:A police report flutters in the wind."
        )
        player.x = 1 // Reset player position for the new zone
        player.y = 1
        return GameZone("City Center", 23, 7,
            layout.map { it.toCharArray() }.toTypedArray(), creatures, interactables)
    }

    /**
     * Creates and configures the "Eerie Hospital" game zone.
     * Sets the player's starting position for this zone.
     * @return The configured [GameZone] object for the Eerie Hospital.
     */
    private fun createHospitalZone(): GameZone {
        val layout = arrayOf(
            "####################",
            "#P....#......M.....#",
            "#.?.C.#............#",
            "#.....#......D.....#",
            "######M############",
            "#..................#",
            "####################"
        )
        val creatures = mutableListOf<Creature>(
            Whisperer(14, 1), Whisperer(6, 4)
        )
        val interactables = mutableMapOf(
            (2 to 2) to "?:A patient's chart with frantic scribbles.",
            (4 to 2) to "C:A medical supply cabinet.",
            (15 to 3) to "D:A door to the security office."
        )
        player.x = 1 // Reset player position
        player.y = 1
        return GameZone("Eerie Hospital", 20, 7,
            layout.map { it.toCharArray() }.toTypedArray(), creatures, interactables)
    }

    /**
     * Creates and configures the "Radio Tower" game zone.
     * Sets the player's starting position for this zone.
     * @return The configured [GameZone] object for the Radio Tower.
     */
    private fun createRadioTowerZone(): GameZone {
        val layout = arrayOf(
            "##########",
            "#P.......#",
            "#........#",
            "#...A....#",
            "#........#",
            "#...E....#", // E for Endgame interactable
            "##########"
        )
        val creatures = mutableListOf<Creature>(Anomaly(4, 3)) // Anomaly at (4,3) based on 'A'
        val interactables = mutableMapOf(
            (4 to 5) to "E:The broadcast equipment. It needs repairs."
        )
        player.x = 1 // Reset player position
        player.y = 1
        return GameZone("Radio Tower", 10, 7,
            layout.map { it.toCharArray() }.toTypedArray(), creatures, interactables)
    }

    /**
     * Reveals a 3x3 area around the player's current position on the map
     * by marking those tiles as visited in the [currentZone].
     */
    private fun revealInitialArea() {
        for (dy in -1..1) {
            for (dx in -1..1) {
                currentZone.markVisited(player.x + dx, player.y + dy)
            }
        }
    }

    /**
     * Starts and manages the main game loop.
     * The loop continues until the [gameOver] flag is true.
     * In each iteration, it prints the game state, handles the player's turn,
     * then handles the creatures' turn and updates player stats if the game is not over.
     */
    fun run() {
        while (!gameOver) {
            printGameState()
            handlePlayerTurn()
            if (!gameOver) { // Check if player's turn ended the game
                handleCreatureTurn()
                updatePlayerStats()
            }
        }
        printGameOver()
    }

    /**
     * Clears the console and prints the current game state.
     * This includes player stats (HP, Hunger, Sanity), the game map,
     * the message log, and available commands.
     * The map is scrambled if player sanity is low.
     */
    private fun printGameState() {
        print("\u001b[H\u001b[2J") // Clears the console
        System.out.flush()
        println("--- Static Decay ---")
        println("HP: ${player.hp}/100 | Hunger: ${player.hunger}/100 | Sanity: ${player.sanity}/100")
        println("-".repeat(60))

        val mapStr = buildMapString()
        if (player.sanity < 30) {
            println(scrambleText(mapStr))
        } else {
            println(mapStr)
        }

        println("-".repeat(30))
        println("LOG:")
        messageLog.takeLast(5).forEach { println("> $it") } // Show last 5 messages
        println("-".repeat(30))
        println("COMMANDS: [W/A/S/D] Move, [I]nventory, [C]raft, [L]ook, [Q]uit")
    }

    /**
     * Constructs a string representation of the current game zone map.
     * Shows player (@), creatures (M for generic, A for Anomaly), and visited tiles.
     * Unvisited tiles are shown as empty spaces.
     * @return A string representing the formatted map.
     */
    private fun buildMapString(): String {
        val sb = StringBuilder()
        for (y in 0 until currentZone.height) {
            for (x in 0 until currentZone.width) {
                val creature = currentZone.creatures.find { it.x == x && it.y == y }
                val isVisible = currentZone.isVisited(x, y)

                if (player.x == x && player.y == y) {
                    sb.append("@")
                } else if (creature != null && isVisible) {
                    sb.append(
                        when (creature) {
                            is Anomaly -> 'A'
                            // Add other specific creature characters here if needed
                            else -> 'M' // Generic monster
                        }
                    )
                } else if (isVisible) {
                    sb.append(currentZone.getTile(x, y))
                } else {
                    sb.append(' ') // Unvisited area
                }
            }
            sb.append("")
        }
        return sb.toString()
    }

    /**
     * Scrambles a given text string by randomly replacing characters.
     * Used to distort the map when player sanity is low.
     * @param text The input string to scramble.
     * @return The scrambled string.
     */
    private fun scrambleText(text: String): String {
        val chars = text.toCharArray()
        for (i in chars.indices) {
            // Only scramble letters and digits, and not newlines or spaces
            if (chars[i].isLetterOrDigit() && Random.nextInt(100) < 20) { // 20% chance to scramble
                chars[i] = "!?#%$*&".random()
            }
        }
        return String(chars)
    }

    /**
     * Handles the player's turn by reading input and performing actions.
     * Possible actions: move (W/A/S/D), inventory (I), craft (C), look (L), quit (Q).
     */
    private fun handlePlayerTurn() {
        print("Your action: ")
        val input = readLine()?.trim()?.uppercase(Locale.ROOT) ?: ""

        if (input.isNotEmpty()) {
            when (input[0]) {
                'W', 'A', 'S', 'D' -> movePlayer(input[0])
                'I' -> showInventory()
                'C' -> showCrafting()
                'L' -> look()
                'Q' -> {
                    log("You give up hope.")
                    gameOver = true
                }
                else -> log("Invalid command.")
            }
        } else {
            log("No command entered.") // Handle empty input
        }
    }

    /**
     * Moves the player in the specified direction if the path is not blocked.
     * Updates player position, hunger, and reveals the new area.
     * If a creature is in the target tile, combat is initiated.
     * If an interactable is at the new location, it's logged.
     * @param dir The character representing the direction of movement (W, A, S, D).
     */
    private fun movePlayer(dir: Char) {
        var newX = player.x
        var newY = player.y

        when (dir) {
            'W' -> newY--
            'A' -> newX--
            'S' -> newY++
            'D' -> newX++
        }

        // Check bounds before getting tile to prevent errors, though getTile handles it.
        if (newX !in 0 until currentZone.width || newY !in 0 until currentZone.height) {
            log("You can't go that way (edge of the world).")
            return
        }

        val targetTile = currentZone.getTile(newX, newY)
        val creature = currentZone.creatures.find { it.x == newX && it.y == newY }

        if (creature != null) {
            startCombat(creature)
            return
        }

        if (targetTile != '#') { // '#' represents a wall
            player.x = newX
            player.y = newY
            player.loseHunger(1) // Moving costs hunger
            // Reveal the 3x3 area around the new player position
            for (dy in -1..1) {
                for (dx in -1..1) {
                    currentZone.markVisited(player.x + dx, player.y + dy)
                }
            }

            val interactable = currentZone.interactables[player.x to player.y]
            if (interactable != null) {
                log("You see something: ${interactable.substringAfter(':')}")
            }
        } else {
            log("A wall blocks your path.")
        }
    }

    /**
     * Allows the player to look at their current location.
     * If an interactable object is present, [handleInteraction] is called.
     * Otherwise, a message indicating nothing interesting is found is logged.
     */
    private fun look() {
        val interactable = currentZone.interactables[player.x to player.y]
        if (interactable != null) {
            handleInteraction(interactable)
        } else {
            log("There's nothing interesting here.")
        }
    }

    /**
     * Handles player interaction with an object at their current location.
     * The type of interaction is determined by a prefix in the interaction string (e.g., 'C' for container).
     * @param interaction The string describing the interactable, including its type prefix.
     */
    private fun handleInteraction(interaction: String) {
        val type = interaction.firstOrNull() ?: return // Safety check for empty interaction string
        val description = interaction.substringAfter(':', "a mysterious object") // Provide default
        log(description)

        when (type) {
            'C' -> { // Container (e.g., cupboard, locker)
                log("You search the ${description.lowercase()}...")
                val foundSomething = when (Random.nextInt(5)) { // Chance to find different items
                    0 -> items["Canned Food"]?.also { player.addItem(it);
                        log("You found canned food!") }
                    1 -> items["Dirty Rags"]?.also { player.addItem(it);
                        log("You found some dirty rags!") }
                    2 -> items["Scrap Metal"]?.also { player.addItem(it);
                        log("You found scrap metal!") }
                    3 -> items["Chemicals"]?.also { player.addItem(it);
                        log("You found chemicals!") }
                    else -> null
                }
                if (foundSomething == null) {
                    log("...it's empty.")
                }
                currentZone.removeInteractable(player.x, player.y) // Container is now empty
            }
            '?' -> { // Story element or special item
                when (currentZone.name) {
                    "Abandoned Subway" -> log("Journal Entry 1: '...static on the radio for days." +
                            " Maria thinks she saw something in the tunnels. I think she's just" +
                            " scared. We have to try for the surface. The radio tower is our" +
                            " only hope.'")

                    "City Center" -> log("Police Report: '...reports of violent, erratic behavior" +
                            " city-wide. Subjects show extreme aggression. Quarantine protocols" +
                            " failing. It's not a riot... it's something else.'")
                    "Eerie Hospital" -> {

                        log("Patient's Chart: 'Patient X exhibits extreme paranoia, muttering" +
                                " about 'whispers in the static'. Physical form is... unstable." +
                                " Rapid cellular decay observed. God help us all.'")

                        items["Security Keycard"]?.let {
                            player.addItem(it)
                            log("You find a Security Keycard in a nearby desk!")
                        }
                    }
                }
                currentZone.removeInteractable(player.x, player.y) // Story element is now read/taken
            }
            'D' -> { // Door or transition point
                when (currentZone.name) {
                    "Abandoned Subway" -> {
                        if (player.hasItem("Crowbar")) {
                            log("You use the crowbar to force the door open! The city air hits you.")
                            currentZone = createCityCenterZone()
                            revealInitialArea()
                        } else {
                            log("It's jammed shut. You need something to pry it open.")
                        }
                    }
                    "City Center" -> {
                        if (player.hasItem("Crowbar")) {
                            log("With a loud groan, the maintenance hatch opens, revealing a dark descent.")
                            currentZone = createHospitalZone()
                            revealInitialArea()
                        } else {
                            log("It's sealed shut. A crowbar might work.")
                        }
                    }
                    "Eerie Hospital" -> {
                        if (player.hasItem("Security Keycard")) {
                            log("The keycard beeps and the lock clicks open. The air feels heavy.")
                            currentZone = createRadioTowerZone()
                            revealInitialArea()
                        } else {
                            log("It's an electronic lock. You need a keycard.")
                        }
                    }
                }
            }
            'E' -> { // Endgame interactable
                if (currentZone.creatures.any { it is Anomaly }) {
                    log("The broadcast equipment is shielded by a strange psychic energy." +
                            " You can't get close!")
                } else {
                    log("With the Anomaly gone, you approach the console. You find enough working" +
                            " parts to send a simple, repeating message: ...'is anyone out there?" +
                            " We are alive. We are at...' You give the coordinates. You've done" +
                            " it. You've sent a message of hope into the static.")

                    println("CONGRATULATIONS! YOU HAVE BEATEN STATIC DECAY!")
                    gameOver = true
                }
            }
            else -> log("You examine the ${description.lowercase()}, but nothing happens.")
        }
    }

    /**
     * Displays the player's inventory and allows them to use or equip items.
     * Player can choose an item by name or go back.
     */
    private fun showInventory() {
        if (player.inventory.isEmpty()) {
            log("Your inventory is empty.")
            return
        }

        println("--- INVENTORY ---")
        player.inventory.forEach { (item, count) ->
            println("$count ${item.name} - ${item.description}")
        }
        println("------------------")

        print("Enter item name to use/equip, or 'B' to go back: ")
        val input = readLine()?.trim()
        if (input.equals("B", ignoreCase = true) || input.isNullOrEmpty()) {
            return
        }

        val itemToUse = player.inventory.keys.find { it.name.equals(input, ignoreCase = true) }

        if (itemToUse != null) {
            when (itemToUse) {
                is Consumable -> {
                    if (player.removeItem(itemToUse)) { // Remove one instance of the item
                        log(itemToUse.effect(player))
                    } else {
                        log("Error: Could not remove ${itemToUse.name} from inventory.") // Should not happen if found
                    }
                }
                is Weapon -> {
                    player.equippedWeapon = itemToUse
                    log("You equipped the ${itemToUse.name}.")
                }
                else -> log("You can't use or equip '${itemToUse.name}' in this way.")
            }
        } else {
            log("You don't have an item named '$input'.")
        }
    }

    /**
     * Displays available crafting recipes and allows the player to craft items.
     * Player can choose a recipe by number or go back.
     * Crafting succeeds if the player has all required ingredients.
     */
    private fun showCrafting() {
        if (recipes.isEmpty()){
            log("There are no crafting recipes available.")
            return
        }
        println("--- CRAFTING ---")
        recipes.forEachIndexed { index, recipe ->
            val ingredientStr = recipe.ingredients.map { (name, count) -> "$name x$count" }.joinToString(", ")
            println("[$index] ${recipe.result.name} - Requires: $ingredientStr")
        }
        println("-----------------")

        print("Enter recipe number to craft, or 'B' to go back: ")
        val inputStr = readLine()?.trim()
        if (inputStr.equals("B", ignoreCase = true) || inputStr.isNullOrEmpty()) {
            return
        }
        val input = inputStr.toIntOrNull()

        if (input != null && input in recipes.indices) {
            val recipe = recipes[input]
            val canCraft = recipe.ingredients.all { (itemName, requiredCount) ->
                val itemInInventory = items[itemName] // Get the Item object
                (itemInInventory != null && player.inventory.getOrDefault(itemInInventory, 0) >= requiredCount)
            }

            if (canCraft) {
                recipe.ingredients.forEach { (itemName, count) ->
                    items[itemName]?.let { player.removeItem(it, count) } // Remove ingredients
                }
                player.addItem(recipe.result) // Add crafted item
                log("You successfully crafted a ${recipe.result.name}!")
            } else {
                log("You don't have the required ingredients for ${recipe.result.name}.")
            }
        } else {
            log("Invalid recipe number.")
        }
    }

    /**
     * Initiates and manages a combat sequence between the player and a creature.
     * Combat continues in turns until either the player or creature HP drops to 0, or the player runs.
     * @param creature The [Creature] the player is fighting.
     */
    private fun startCombat(creature: Creature) {
        log("You encounter a ${creature.name}!")
        var combatOver = false

        while (!combatOver && creature.hp > 0 && player.hp > 0) {
            println("--- COMBAT ---")
            println("${creature.name} HP: ${creature.hp}")
            println("Your HP: ${player.hp}")
            println("Actions: [A]ttack, [I]tem, [R]un")
            print("Your choice: ")

            val action = readLine()?.trim()?.uppercase(Locale.ROOT) ?: ""
            var playerActed = false // Flag to ensure player action is processed before creature attacks

            if (action.isNotEmpty()) {
                when (action[0]) {
                    'A' -> {
                        val damage = player.equippedWeapon?.damage ?: 5 // Base damage if no weapon
                        creature.takeDamage(damage)
                        log("You attack the ${creature.name} for $damage damage.")
                        playerActed = true
                    }
                    'I' -> {
                        println("--- Usable Items ---")
                        val consumables = player.inventory.keys.filterIsInstance<Consumable>()
                        if (consumables.isEmpty()) {
                            log("You have no consumable items to use in combat.")
                        }
                        else {
                            consumables.forEach { println("- ${it.name}") }
                            print("Use which item? (Type name or B to go back): ")
                            val itemInput = readLine()?.trim()
                            if (!itemInput.equals("B", ignoreCase = true)
                                && !itemInput.isNullOrEmpty()) {
                                val itemToUse = consumables
                                    .find { it.name.equals(itemInput, ignoreCase = true) }
                                if (itemToUse != null) {
                                    if (player.removeItem(itemToUse)) {
                                        log(itemToUse.effect(player))
                                        playerActed = true
                                    }
                                } else {
                                    log("Invalid item or you don't have it.")
                                }
                            } else {
                                log("Cancelled using item.")
                            }
                        }
                    }
                    'R' -> {
                        if (Random.nextInt(100) < 40) { // 40% chance to escape
                            log("You successfully escaped!")
                            combatOver = true // End combat
                        } else {
                            log("You failed to escape!")
                            playerActed = true // Failed escape means creature gets a turn
                        }
                    }
                    else -> log("Invalid combat action.")
                }
            } else {
                log("No action taken.")
            }

            if (combatOver) continue // Skip creature turn if player escaped

            // Creature's turn if it's still alive and player acted (or failed to escape)
            if (playerActed && creature.hp > 0) {
                if (creature is Whisperer) {
                    val sanityDamage = 20
                    player.loseSanity(sanityDamage)
                    log("The ${creature.name}'s whispers echo in your mind!" +
                            " You lose $sanityDamage sanity.")
                }
                // Standard attack if not a Whisperer or if Whisperer also has a standard attack
                if (creature.attack > 0) { // Check if creature has an attack value
                    player.takeDamage(creature.attack)
                    log("The ${creature.name} attacks you for ${creature.attack} damage.")
                }


                if (player.hp <= 0) {
                    log("You have been defeated by the ${creature.name}!")
                    gameOver = true
                    combatOver = true
                }
            }
        }

        if (creature.hp <= 0 && !gameOver) { // Check !gameOver in case player died simultaneously
            log("You defeated the ${creature.name}!")
            currentZone.creatures.remove(creature)
        }
    }

    /**
     * Handles the turn for all creatures in the current zone.
     * Creatures within a certain proximity (Manhattan distance < 5) will attempt
     * to move towards the player.
     * They will not move into walls or tiles occupied by other creatures.
     */
    private fun handleCreatureTurn() {
        currentZone.creatures.forEach { creature ->
            if (creature is Anomaly) return@forEach // Anomalies are stationary

            // Calculate Manhattan distance
            val distance = kotlin.math.abs(creature.x - player.x)
            + kotlin.math.abs(creature.y - player.y)

            if (distance < 5 && distance > 0) { // Creature is close but not on the same tile
                var dx = 0
                var dy = 0

                // Simple pathfinding: move one step towards player
                if (player.x < creature.x) dx = -1
                else if (player.x > creature.x) dx = 1

                if (player.y < creature.y) dy = -1
                else if (player.y > creature.y) dy = 1
                
                // Attempt to move horizontally first, then vertically if horizontal
                // is blocked or not needed
                var moved = false
                if (dx != 0) {
                    val nextX = creature.x + dx
                    val nextY = creature.y
                     if (currentZone.getTile(nextX, nextY) != '#' &&
                        currentZone.creatures.none { it.x == nextX && it.y == nextY
                                && it !== creature} && !(player.x == nextX && player.y == nextY)) {
                        creature.x = nextX
                        moved = true
                    }
                }

                if (!moved && dy != 0) { // If didn't move horizontally, try vertically
                    val nextX = creature.x // Use current x if only moving vertically
                    val nextY = creature.y + dy
                     if (currentZone.getTile(nextX, nextY) != '#' &&
                        currentZone.creatures.none { it.x == nextX && it.y == nextY
                                && it !== creature} && !(player.x == nextX && player.y == nextY) ) {
                        creature.y = nextY
                    }
                }
            }
        }
    }

    /**
     * Updates player stats that change over time, such as sanity loss in certain zones.
     * Also checks for game over conditions related to player stats (e.g., HP <= 0).
     */
    private fun updatePlayerStats() {
        if (player.hp <= 0) {
            // Game over due to HP loss is typically handled in combat or other damage sources,
            // but this is a final check.
            if (!gameOver) { // Avoid redundant game over messages
                 log("Your wounds are too severe. You succumb to the darkness.")
                 gameOver = true
            }
            return // No further stat updates if player is already down
        }

        // Sanity drain in specific zones
        if (currentZone.name == "Eerie Hospital" || currentZone.name == "Abandoned Subway") {
            if (player.sanity > 0) {
                log("The oppressive atmosphere wears on your mind.")
                player.loseSanity(2)
                if (player.sanity == 0) {
                    log("Your mind shatters under the strain!")
                    // Potentially trigger other effects for zero sanity
                }
            }
        }
        // Hunger drain could be placed here if it's time-based rather than action-based
        // player.loseHunger(1) // e.g. every few turns
    }

    /**
     * Adds a message to the game's [messageLog].
     * @param message The string message to log.
     */
    fun log(message: String) {
        messageLog.add(message)
        // Optionally, could cap the messageLog size here if it grows too large
        // if (messageLog.size > 100) messageLog.removeFirst()
    }

    /**
     * Prints the game over message to the console, including the last message from the log.
     */
    private fun printGameOver() {
        println(" ==================")
        println("GAME OVER")
        println("==================")
        if (messageLog.isNotEmpty()) {
            println(messageLog.last()) // Print the last relevant message
        }
    }
}
