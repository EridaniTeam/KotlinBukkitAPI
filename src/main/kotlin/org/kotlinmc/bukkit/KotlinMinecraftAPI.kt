package org.kotlinmc.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.kotlinmc.bukkit.controllers.*
import org.kotlinmc.bukkit.extensions.registerEvents

internal fun provideKotlinBukkitAPI(): KotlinMinecraftAPI {
    return Bukkit.getServer().pluginManager.getPlugin("KotlinMinecraftBukkit") as KotlinMinecraftAPI?
        ?: throw IllegalAccessException("The plugin KotlinMinecraftBukkit is not loaded yet")
}

class KotlinMinecraftAPI : JavaPlugin() {
    init {
        me.bristermitten.pdm.SpigotDependencyManager.of(this).loadAllDependencies().thenRun {
            logger.info("Kotlin Minecraft API dependency is loaded!")
        }
    }

    internal val commandController = CommandController(this)
    internal val menuController = MenuController(this)
    internal val playerController = PlayerController(this)
    internal val providerController = ProviderController(this)
    internal val bungeeCordController = BungeeCordController(this)

    private val controllers = listOf<KBAPIController>(
        commandController, menuController, playerController,
        providerController, bungeeCordController
    )

    override fun onEnable() {
        for (controller in controllers) {
            controller.onEnable()

            if (controller is Listener)
                registerEvents(controller)
        }
    }
}