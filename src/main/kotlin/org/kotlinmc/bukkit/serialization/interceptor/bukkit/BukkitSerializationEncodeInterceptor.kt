package org.kotlinmc.bukkit.serialization.interceptor.bukkit

import kotlinx.serialization.descriptors.SerialDescriptor
import org.kotlinmc.bukkit.extensions.reverseTranslateColor
import org.kotlinmc.bukkit.serialization.ChangeColor
import org.kotlinmc.bukkit.serialization.interceptor.*

object BukkitSerializationEncodeInterceptor : SerializationEncodeInterceptor by ClearSerializationEncodeInterceptor() {

    override fun encodeString(descriptor: SerialDescriptor, index: Int, value: String): String {
        return useChangeColor(descriptor, index, value)
    }

    private fun useChangeColor(descriptor: SerialDescriptor, index: Int, value: String): String {
        descriptor.findElementAnnotation<ChangeColor>(index) ?: return value

        return value.reverseTranslateColor()
    }
}