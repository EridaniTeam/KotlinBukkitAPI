package org.kotlinmc.bukkit.serialization.interceptor.impl.decoder

import org.kotlinmc.bukkit.serialization.interceptor.SerializationDecodeInterceptor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encoding.Decoder

class DeserializationStrategyInterceptor<T>(
    val interceptor: SerializationDecodeInterceptor,
    val delegate: DeserializationStrategy<T>
) : DeserializationStrategy<T> by delegate {
    override fun deserialize(decoder: Decoder): T {
        return delegate.deserialize(DecoderInterceptor(interceptor, decoder))
    }
}