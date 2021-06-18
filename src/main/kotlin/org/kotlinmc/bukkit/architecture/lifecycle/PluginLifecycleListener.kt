package org.kotlinmc.bukkit.architecture.lifecycle

enum class LifecycleEvent { LOAD, ENABLE, DISABLE, CONFIG_RELOAD, ALL_CONFIG_RELOAD }

typealias PluginLifecycleListener = (LifecycleEvent) -> Unit