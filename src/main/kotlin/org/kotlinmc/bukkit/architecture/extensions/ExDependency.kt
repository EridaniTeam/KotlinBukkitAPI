package org.kotlinmc.bukkit.architecture.extensions

import org.kotlinmc.bukkit.architecture.KotlinPlugin
import org.bukkit.plugin.Plugin
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Delegate that returns the plugin dependency if the plugin is installed in the server
 * otherwise, returns null
 */
inline fun <reified T : Plugin> KotlinPlugin.softDepend(
    pluginName: String
) = softDepend(T::class, pluginName)

fun <T : Plugin> KotlinPlugin.softDepend(
    type: KClass<T>,
    pluginName: String
): SoftDependencyDelegate<T> =
    SoftDependencyDelegate(
        pluginName,
        type
    )

/**
 * Delegate that returns the plugin dependency, disable the plugin if the plugin
 * is not available.
 */
inline fun <reified T : Plugin> KotlinPlugin.depend(
    pluginName: String
) = depend(T::class, pluginName)

fun <T : Plugin> KotlinPlugin.depend(
    type: KClass<T>,
    pluginName: String
): DependencyDelegate<T> =
    DependencyDelegate(pluginName, type)











class DependencyDelegate<T : Plugin>(
    val pluginName: String,
    val type: KClass<T>
) : ReadOnlyProperty<KotlinPlugin, T> {

    private var isDisabled: Boolean = false
    private var cache: T? = null

    override fun getValue(
        thisRef: KotlinPlugin,
        property: KProperty<*>
    ): T {
        if(cache == null) {
            val plugin = thisRef.server.pluginManager.getPlugin(pluginName)

            if (plugin != null) {
                if (type.isInstance(plugin)) {
                    cache = plugin as T
                } else {
                    thisRef.server.pluginManager.disablePlugin(thisRef)
                    error(
                        "Invalid plugin dependency with the name $pluginName: " +
                                "The plugin do not match main class with ${type.qualifiedName}."
                    )
                }
            } else {
                thisRef.server.pluginManager.disablePlugin(thisRef)
                error("Missing plugin dependency: $pluginName")
            }
        }

        return cache!!
    }
}

class SoftDependencyDelegate<T : Plugin>(
    val pluginName: String,
    val type: KClass<T>
) : ReadOnlyProperty<KotlinPlugin, T?> {

    private var alreadySearch: Boolean = false
    private var cache: T? = null

    override fun getValue(
        thisRef: KotlinPlugin,
        property: KProperty<*>
    ): T? {
        if(!alreadySearch) {
            val plugin = thisRef.server.pluginManager.getPlugin(pluginName) ?: return null

            alreadySearch = true

            if (type.isInstance(plugin)) {
                cache = plugin as T
            } else {
                thisRef.server.pluginManager.disablePlugin(thisRef)
                error(
                    "Invalid plugin dependency with the name $pluginName: " +
                            "The plugin do not match main class with ${type.qualifiedName}."
                )
            }
        }

        return cache
    }
}

