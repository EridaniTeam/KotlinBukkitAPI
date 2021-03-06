package org.kotlinmc.bukkit.architecture.lifecycle.extensions

import org.kotlinmc.bukkit.architecture.KotlinPlugin
import org.kotlinmc.bukkit.architecture.lifecycle.LifecycleListener
import org.kotlinmc.bukkit.architecture.lifecycle.extensions.impl.getOrInsertCoroutineLifecycle
import kotlinx.coroutines.*
import org.bukkit.entity.Player

/**
 * A CoroutineScope that trigger in the Main Thread of Bukkit by default.
 *
 * This scope ensures that your task will be canceled when the plugin disable
 * removing the possibility of Job leaks.
 */
val LifecycleListener<*>.pluginCoroutineScope: CoroutineScope
    get() = plugin.pluginCoroutineScope

val KotlinPlugin.pluginCoroutineScope: CoroutineScope
    get() = getOrInsertCoroutineLifecycle().pluginCoroutineScope

/**
 * A CoroutineScope that trigger in the Main Thread of Bukkit by default.
 *
 * This scope ensures that your task will be canceled when the plugin disable
 * and when the [player] disconnect the server removing the possibility of Job leaks.
 */
fun LifecycleListener<*>.playerCoroutineScope(player: Player): CoroutineScope {
    return plugin.playerCoroutineScope(player)
}

fun KotlinPlugin.playerCoroutineScope(player: Player): CoroutineScope {
    return getOrInsertCoroutineLifecycle().getPlayerCoroutineScope(player)
}
