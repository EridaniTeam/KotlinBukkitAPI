package org.kotlinmc.bukkit.architecture.lifecycle

import org.kotlinmc.bukkit.architecture.KotlinPlugin

inline fun <reified T : PluginLifecycleListener> KotlinPlugin.getOrInsertGenericLifecycle(
        priority: Int,
        factory: () -> T
): T {
    return lifecycleListeners
            .map { it.listener }
            .filterIsInstance<T>()
            .firstOrNull()
            ?: factory().also { registerKotlinPluginLifecycle(priority, it) }
}