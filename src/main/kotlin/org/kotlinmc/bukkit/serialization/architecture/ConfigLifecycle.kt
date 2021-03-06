package org.kotlinmc.bukkit.serialization.architecture

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import org.kotlinmc.bukkit.architecture.KotlinPlugin
import org.kotlinmc.bukkit.architecture.lifecycle.LifecycleListener
import org.kotlinmc.bukkit.serialization.*
import org.kotlinmc.bukkit.serialization.architecture.impl.*
import java.io.File
import kotlin.reflect.*

/**
 * Loads the file with the given [serializer].
 *
 * If the file not exist, one will be created with the [defaultModel] serialize into it.
 *
 * If [KotlinPlugin.reloadConfig] get called will reload the Config.
 *
 * KotlinBukkitAPI provide Kotlinx.Serialization Contextual Serializers for a couple Bukkit objects,
 * this is provided by the BukkitSerialModule(), if do you have a custom SerialModule you should add
 * the BukkitSerialModule by `yourSerialModule + BukkitSerialModule()`. The provided Bukkit types are:
 * Block, Chunk, Location, Material, MaterialData, World.
 *
 * Custom annotations:
 * - `@ChangeColor` in a String property translate the color codes from the Configuration, saves in `&` and loads in ``.
 *
 * @param file: The file name in your [dataFolder] (like config.yml).
 * @param loadOnEnable: If true, loads your configuration just when the server enable,
 * otherwise, load at the call of this function. This could be usage if your configuration
 * uses a parser that Parser a Location or a World that is not loaded yet.
 * @param saveOnDisable: If true, saves the current [SerializationConfig.model] to the configuration file.
 */
fun <T : Any> KotlinPlugin.config(
    file: String,
    defaultModel: T,
    serializer: KSerializer<T>,
    type: StringFormat = Yaml(BukkitSerialModule()),
    loadOnEnable: Boolean = false,
    saveOnDisable: Boolean = false,
    alwaysRestoreDefaults: Boolean = true
): SerializationConfig<T> {
    val configFile = File(dataFolder, file)

    return SerializationConfig(
        defaultModel,
        configFile,
        serializer,
        type,
        alwaysRestoreDefaults,
        eventObservable = {
            if (it == KotlinConfigEvent.RELOAD)
                someConfigReloaded()
        }
    ).also {
        registerConfiguration(it as SerializationConfig<Any>, loadOnEnable, saveOnDisable)
    }
}

/**
 * Gets the config for the given [KType]
 */
fun LifecycleListener<*>.getConfig(type: KType): SerializationConfig<*> {
    try {
        val serialName = serializer(type).descriptor.serialName
        val config = plugin.getOrInsertConfigLifecycle().serializationConfigurations[serialName]

        requireNotNull(config) { "Could not find this type registred as a Config." }

        return config
    } catch (e: SerializationException) {
        throw IllegalArgumentException("The config given type is not a serializable one.")
    }
}


/**
 * Config delegate that caches the config reference.
 */

fun <T : Any> LifecycleListener<*>.config(type: KType): ConfigDelegate<T, T> {
    return config(type, { this })
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> LifecycleListener<*>.config(): ConfigDelegate<T, T> = config<T>(typeOf<T>())

fun <T : Any, R> LifecycleListener<*>.config(
    type: KType,
    deep: T.() -> R
): ConfigDelegate<T, R> {
    return ConfigDelegate(type, deep)
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any, R> LifecycleListener<*>.config(
    noinline deep: T.() -> R
): ConfigDelegate<T, R> = config(typeOf<T>(), deep)