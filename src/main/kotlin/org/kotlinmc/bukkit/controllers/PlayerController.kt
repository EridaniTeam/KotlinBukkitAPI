package org.kotlinmc.bukkit.controllers


import org.kotlinmc.bukkit.extensions.KListener
import org.kotlinmc.bukkit.extensions.displaced
import org.kotlinmc.bukkit.extensions.event
import org.kotlinmc.bukkit.extensions.scheduler

import org.kotlinmc.bukkit.utils.player.ChatInput
import org.kotlinmc.bukkit.utils.player.PlayerCallback
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.server.PluginDisableEvent
import org.kotlinmc.bukkit.*
import org.kotlinmc.bukkit.collections.onlinePlayerMapOf
import org.kotlinmc.bukkit.provideKotlinBukkitAPI

internal fun providePlayerController() = provideKotlinBukkitAPI().playerController

internal class PlayerController(
        override val plugin: KotlinMinecraftAPI
) : KListener<KotlinMinecraftAPI>, KBAPIController {

    internal val inputCallbacks by lazy { plugin.onlinePlayerMapOf<ChatInput>() }
    internal val functionsMove by lazy { plugin.onlinePlayerMapOf<PlayerCallback<Boolean>>() }
    internal val functionsQuit by lazy { plugin.onlinePlayerMapOf<PlayerCallback<Unit>>() }

    override fun onEnable() {
        event<AsyncPlayerChatEvent>(ignoreCancelled = true) {
            if (message.isNotBlank()) {
                val input = inputCallbacks.remove(player)
                if (input != null) {
                    if (input.sync) scheduler { input.callback(player, message) }.runTask(plugin)
                    else input.callback(player, message)
                    isCancelled = true
                }
            }
        }
        event<PlayerMoveEvent>(ignoreCancelled = true) {
            if (displaced) {
                if (functionsMove[player]?.run { callback.invoke(player) } == true) {
                    isCancelled = true
                }
            }
        }
        event<PluginDisableEvent> {
            inputCallbacks.entries.filter { it.value.plugin == plugin }.forEach {
                inputCallbacks.remove(it.key)
            }
            functionsMove.entries.filter { it.value.plugin == plugin }.forEach {
                functionsMove.remove(it.key)
            }
            functionsQuit.entries.filter { it.value.plugin == plugin }.forEach {
                functionsQuit.remove(it.key)
            }
        }
    }
}