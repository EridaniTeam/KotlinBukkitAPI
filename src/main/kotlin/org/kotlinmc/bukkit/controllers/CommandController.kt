package org.kotlinmc.bukkit.controllers

import org.kotlinmc.bukkit.extensions.unregister
import org.kotlinmc.bukkit.extensions.KListener
import org.kotlinmc.bukkit.extensions.event

import org.bukkit.command.Command
import org.bukkit.event.server.PluginDisableEvent
import org.kotlinmc.bukkit.*
import org.kotlinmc.bukkit.provideKotlinBukkitAPI

internal fun provideCommandController() = provideKotlinBukkitAPI().commandController

internal class CommandController(
        override val plugin: KotlinMinecraftAPI
) : KListener<KotlinMinecraftAPI>, KBAPIController {

    val commands = hashMapOf<String, MutableList<Command>>()

    override fun onEnable() {
        event<PluginDisableEvent> {
            commands.remove(plugin.name)?.forEach {
                it.unregister()
            }
        }
    }
}