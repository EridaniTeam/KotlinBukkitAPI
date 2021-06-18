package org.kotlinmc.bukkit.serialization.serializers

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.bukkit.Material
import org.bukkit.material.MaterialData
import org.kotlinmc.bukkit.extensions.asMaterialData
import java.util.*

@Serializer(forClass = MaterialData::class)
object MaterialDataSerializer : KSerializer<MaterialData> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "org.bukkit.material.MaterialData", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): MaterialData {
        return fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: MaterialData) {
        encoder.encodeString(toString(value))
    }

    private fun toString(material: MaterialData): String {
        return "${material.itemTypeId}:${material.data}"
    }

    private fun fromString(content: String): MaterialData {
        val slices = content.split(":")

        val material = slices[0]
        val data = slices.getOrNull(1)?.toIntOrNull() ?: 0

        return (material.toIntOrNull()?.let {
            Material.getMaterial(it)
        } ?: Material.getMaterial(material.uppercase(Locale.getDefault()))).asMaterialData(data.toByte())
    }

}