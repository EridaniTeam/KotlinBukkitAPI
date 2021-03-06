package org.kotlinmc.bukkit.serialization.interceptor.impl.decoder

import org.kotlinmc.bukkit.serialization.interceptor.SerializationDecodeInterceptor
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

class DecoderInterceptor(
    val interceptor: SerializationDecodeInterceptor,
    val delegate: Decoder
) : Decoder by delegate {
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        return deserializer.deserialize(this)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return CompositeDecoderInterceptor(interceptor, delegate.beginStructure(descriptor))
    }

}