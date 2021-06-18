package org.kotlinmc.bukkit.serialization.architecture.impl


import org.kotlinmc.bukkit.serialization.architecture.getConfig
import org.kotlinmc.bukkit.architecture.KotlinPlugin
import org.kotlinmc.bukkit.architecture.lifecycle.*
import org.kotlinmc.bukkit.extensions.WithPlugin
import org.kotlinmc.bukkit.serialization.SerializationConfig
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal fun KotlinPlugin.getOrInsertConfigLifecycle(): ConfigLifecycle {
    // MAX_VALUE = the last to execute onDisable and the first to loaded
    return getOrInsertGenericLifecycle(Int.MAX_VALUE) {
        ConfigLifecycle(this)
    }
}

internal class ConfigLifecycle(
        override val plugin: KotlinPlugin
) : PluginLifecycleListener, WithPlugin<KotlinPlugin> {
    // String = Descriptor name
    internal val serializationConfigurations = hashMapOf<String, SerializationConfig<Any>>()

    internal val onEnableLoadSerializationConfigurations = mutableListOf<SerializationConfig<*>>()
    internal val onDisableSaveSerializationConfigurations = mutableListOf<SerializationConfig<*>>()

    override fun invoke(event: LifecycleEvent) {
        when(event) {
            LifecycleEvent.ENABLE -> onPluginEnable()
            LifecycleEvent.DISABLE -> onPluginDisable()
            LifecycleEvent.ALL_CONFIG_RELOAD -> onConfigReload()
        }
    }

    fun onPluginEnable() {
        for (config in onEnableLoadSerializationConfigurations)
            config.load()
    }

    fun onPluginDisable() {
        for (config in onDisableSaveSerializationConfigurations)
            config.save()
    }

    fun onConfigReload() {
        for (config in serializationConfigurations.values)
            config.reload()
    }
}

internal fun KotlinPlugin.registerConfiguration(
        config: SerializationConfig<Any>,
        loadOnEnable: Boolean,
        saveOnDisable: Boolean
) {
    val lifecycle = getOrInsertConfigLifecycle()

    lifecycle.serializationConfigurations.put(config.serializer.descriptor.serialName, config)

    if(loadOnEnable) {
        val configLifecycle = getOrInsertConfigLifecycle()
        configLifecycle.onEnableLoadSerializationConfigurations.add(config)
    } else {
        config.load()
    }

    if(saveOnDisable) {
        val configLifecycle = getOrInsertConfigLifecycle()
        configLifecycle.onDisableSaveSerializationConfigurations.add(config)
    }
}

class ConfigDelegate<T, R>(
        val type: KType,
        val deep: T.() -> R
) : ReadOnlyProperty<LifecycleListener<*>, R> {
    private var configCache: SerializationConfig<*>? = null

    override fun getValue(
            thisRef: LifecycleListener<*>,
            property: KProperty<*>
    ): R {
        if(configCache == null) {
            val config = thisRef.getConfig(type)

            configCache = config
            // TODO: listen when the config reloads and update the cache value
        }

        return deep(configCache!!.config as T)
    }
}