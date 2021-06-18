package org.kotlinmc.bukkit.extensions

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.*
import org.bukkit.plugin.*
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scoreboard.ScoreboardManager
import java.io.File
import java.util.*

val isPrimaryThread get() = server.isPrimaryThread

fun offlinePlayer(uuid: UUID): OfflinePlayer = server.getOfflinePlayer(uuid)
fun offlinePlayer(name: String): OfflinePlayer = server.getOfflinePlayer(name)

fun player(uuid: UUID): Player? = server.getPlayer(uuid)
fun player(name: String): Player? = server.getPlayer(name)
fun playerExact(name: String): Player? = server.getPlayerExact(name)
fun matchPlayer(name: String): List<Player> = matchPlayer(name)

fun onlinePlayer(uuid: UUID): Player? = server.getPlayer(uuid)
fun onlinePlayer(name: String): Player? = server.getPlayerExact(name)

val onlinePlayers: Collection<Player> get() = server.onlinePlayers
val worldType: String get() = server.worldType
val generateStructures: Boolean get() = server.generateStructures
val allowEnd: Boolean get() = server.allowEnd
val allowNether: Boolean get() = server.allowNether
val allowFlight: Boolean get() = server.allowFlight
val whitelistedPlayers: MutableSet<OfflinePlayer> get() = server.whitelistedPlayers
val updateFolder: String get() = server.updateFolder
val updateFolderFile: File get() = server.updateFolderFile
val connectionThrottle: Long get() = server.connectionThrottle
val recipes: Iterator<Recipe> get() = server.recipeIterator()
val ticksPerAnimalSpawns: Int get() = server.ticksPerAnimalSpawns
val ticksPerMonsterSpawns: Int get() = server.ticksPerMonsterSpawns
val pluginManager: PluginManager get() = server.pluginManager
val scheduler: BukkitScheduler get() = server.scheduler
val servicesManager: ServicesManager get() = server.servicesManager
val worlds: List<World> get() = server.worlds
val onlineMode: Boolean get() = server.onlineMode
val isHardcore: Boolean get() = server.isHardcore
val bannedPlayers: Set<OfflinePlayer> get() = server.bannedPlayers
val ipBans: Set<String> get() = server.ipBans
val operators: Set<OfflinePlayer> get() = server.operators
val worldContainer: File get() = server.worldContainer
val messenger: Messenger get() = server.messenger
val monsterSpawnLimit: Int get() = server.monsterSpawnLimit
val animalSpawnLimit: Int get() = server.animalSpawnLimit
val ambientSpawnLimit: Int get() = server.ambientSpawnLimit
val scoreboardManager: ScoreboardManager get() = server.scoreboardManager

var idleTimeout: Int
    get() = idleTimeout
    set(value) {
        idleTimeout = value
    }

var defaultGameMode: GameMode
    get() = defaultGameMode
    set(value) {
        defaultGameMode = value
    }

var spawnRadius: Int
    get() = spawnRadius
    set(value) {
        spawnRadius = value
    }

var whitelist: Boolean
    get() = org.kotlinmc.bukkit.extensions.server.hasWhitelist()
    set(value) {
        org.kotlinmc.bukkit.extensions.server.setWhitelist(value)
    }

fun reloadWhitelist() = org.kotlinmc.bukkit.extensions.server.reloadWhitelist()

fun world(name: String): World? = org.kotlinmc.bukkit.extensions.server.getWorld(name)

fun addRecipe(recipe: Recipe): Boolean = addRecipe(recipe)
fun recipesFor(item: ItemStack): List<Recipe> = org.kotlinmc.bukkit.extensions.server.getRecipesFor(item)

fun WorldCreator.create(): World = org.kotlinmc.bukkit.extensions.server.createWorld(this)