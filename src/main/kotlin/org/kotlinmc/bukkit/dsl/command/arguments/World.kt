package org.kotlinmc.bukkit.dsl.command.arguments

import org.kotlinmc.bukkit.dsl.command.*
import org.kotlinmc.bukkit.extensions.color
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

// WORLD

val MISSING_WORLD_ARGUMENT = "Missing a world argument.".color(RED)
val WORLD_NOT_FOUND = "World typed not found.".color(RED)

/**
 * Returns [World] or null if was not found.
 */
fun Executor<*>.worldOrNull(
        index: Int,
        argMissing: BaseComponent = MISSING_WORLD_ARGUMENT
): World? = string(index, argMissing).let { Bukkit.getWorld(it) }

fun Executor<*>.world(
        index: Int,
        argMissing: BaseComponent = MISSING_WORLD_ARGUMENT,
        notFound: BaseComponent = WORLD_NOT_FOUND
): World = worldOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.world(
        index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Bukkit.getWorlds().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name else null
    }
}

// COORDINATE

val MISSING_COORDINATE_ARGUMENT = "Missing coordinate argument. Argument format [x] [y] [z]".color(RED)
val COORDINATE_NUMBER_FORMAT = "Please, type numbers for coordinates".color(RED)

fun Executor<Player>.coordinate(
        xIndex: Int, yIndex: Int, zIndex: Int,
        argMissing: BaseComponent = MISSING_COORDINATE_ARGUMENT,
        numberFormat: BaseComponent = COORDINATE_NUMBER_FORMAT
): Location = coordinate(xIndex, yIndex, zIndex, sender.world, argMissing, numberFormat)

fun Executor<*>.coordinate(
        xIndex: Int, yIndex: Int, zIndex: Int, world: World,
        argMissing: BaseComponent = MISSING_COORDINATE_ARGUMENT,
        numberFormat: BaseComponent = COORDINATE_NUMBER_FORMAT
): Location {

    fun double(index: Int) = double(index, argMissing, numberFormat)

    return Location(world, double(xIndex), double(yIndex), double(zIndex))
}