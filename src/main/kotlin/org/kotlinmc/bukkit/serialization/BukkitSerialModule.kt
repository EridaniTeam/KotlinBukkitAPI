package org.kotlinmc.bukkit.serialization

import kotlinx.serialization.modules.*
import org.kotlinmc.bukkit.serialization.serializers.*

fun BukkitSerialModule() = SerializersModule {
    contextual(BlockSerializer)
    contextual(ChunkSerializer)
    contextual(LocationSerializer)
    contextual(MaterialDataSerializer)
    contextual(MaterialSerializer)
    contextual(WorldSerializer)
}