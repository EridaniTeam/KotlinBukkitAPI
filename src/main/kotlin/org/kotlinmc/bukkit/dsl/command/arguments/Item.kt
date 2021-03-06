package org.kotlinmc.bukkit.dsl.command.arguments

import org.kotlinmc.bukkit.dsl.command.*
import org.kotlinmc.bukkit.extensions.asMaterialData
import org.kotlinmc.bukkit.extensions.color
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.material.MaterialData
import java.util.*

// MATERIAL

val MATERIAL_NOT_FOUND = "The item specified not found.".color(ChatColor.RED)
val MATERIAL_MISSING_PARAMETER = "Missing item argument.".color(ChatColor.RED)

private fun toMaterial(string: String) = string.toIntOrNull()?.let { Material.getMaterial(it) }
        ?: Material.getMaterial(string.uppercase(Locale.getDefault()))

/**
 * Returns [Material] or null if the Material was not found.
 */
fun Executor<*>.materialOrNull(
        index: Int,
        argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER
): Material? = string(index, argMissing).run {
    toMaterial(this)
}

fun Executor<*>.material(
        index: Int,
        argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
        notFound: BaseComponent = MATERIAL_NOT_FOUND
): Material = materialOrNull(index, argMissing) ?: fail(notFound)

fun TabCompleter.material(
        index: Int
): List<String> = argumentCompleteBuilder(index) { arg ->
    Material.values().mapNotNull {
        if(it.name.startsWith(arg, true)) it.name.lowercase(Locale.getDefault()) else null
    }
}

// MATERIAL DATA

val DATA_FORMAT = "The item data need be in number.".color(ChatColor.RED)

/**
 * Returns [MaterialData] or null if the Material was not found.
 */
fun Executor<*>.materialDataOrNull(
        index: Int,
        argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
        dataFormat: BaseComponent = DATA_FORMAT
): MaterialData? = string(index, argMissing).run {
    val sliced = this.split(":")
    sliced.getOrNull(1)?.run {
        (toMaterial(sliced[0]))
                ?.asMaterialData(toIntOrNull()?.toByte() ?: fail(dataFormat))
    } ?: materialOrNull(index, argMissing)?.asMaterialData()
}

fun Executor<*>.materialData(
        index: Int,
        argMissing: BaseComponent = MATERIAL_MISSING_PARAMETER,
        notFound: BaseComponent = MATERIAL_NOT_FOUND,
        dataFormat: BaseComponent = DATA_FORMAT
): MaterialData = materialDataOrNull(index, argMissing, dataFormat) ?: fail(notFound)
