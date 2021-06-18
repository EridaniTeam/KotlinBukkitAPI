package org.kotlinmc.bukkit.dsl.command.arguments

import org.kotlinmc.bukkit.dsl.command.Executor
import org.kotlinmc.bukkit.dsl.command.fail
import org.kotlinmc.bukkit.extensions.color
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor

// intRange
val MISSING_RANGE_PARAMETER = "Missing a range argument.".color(ChatColor.RED)
val INT_RANGE_FORMAT = "The parameter needs a range of integer.".color(ChatColor.RED)

/**
 * Returns [IntRange] or null if was not able to parse to IntRange given the [separator].
 */
fun Executor<*>.intRangeOrNull(
        index: Int,
        argMissing: BaseComponent = MISSING_RANGE_PARAMETER,
        separator: String = ".."
): IntRange? {
    val slices = string(index, argMissing).split(separator)
    val min = slices.getOrNull(0)?.toIntOrNull()
    val max = slices.getOrNull(1)?.toIntOrNull()

    return max?.let { min?.rangeTo(it) }
}

fun Executor<*>.intRange(
        index: Int,
        argMissing: BaseComponent = MISSING_RANGE_PARAMETER,
        rangeFormat: BaseComponent = INT_RANGE_FORMAT,
        separator: String = ".."
): IntRange = intRangeOrNull(index, argMissing, separator)
        ?: fail(rangeFormat)
