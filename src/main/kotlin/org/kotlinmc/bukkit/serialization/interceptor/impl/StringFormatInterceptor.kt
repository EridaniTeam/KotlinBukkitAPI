package org.kotlinmc.bukkit.serialization.interceptor.impl

import org.kotlinmc.bukkit.serialization.interceptor.SerializationDecodeInterceptor
import org.kotlinmc.bukkit.serialization.interceptor.SerializationEncodeInterceptor
import org.kotlinmc.bukkit.serialization.interceptor.impl.decoder.DeserializationStrategyInterceptor
import org.kotlinmc.bukkit.serialization.interceptor.impl.encoder.SerializationStrategyInterceptor
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat

class StringFormatInterceptor(
    private val delegate: StringFormat,
    private val encodeInterceptor: SerializationEncodeInterceptor,
    private val decodeInterceptor: SerializationDecodeInterceptor
) : StringFormat by delegate {

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, raw: String): T {
        return delegate.decodeFromString(DeserializationStrategyInterceptor(decodeInterceptor, deserializer), raw)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        return delegate.encodeToString(SerializationStrategyInterceptor(encodeInterceptor, serializer), value)
    }
}