package io.github.zhefimenko.debezium.demo.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

/**
 * @author Yauheni Yefimenka
 */

private val json = Json { ignoreUnknownKeys = true }

class JsonSerializer<T>(
    private val serializer: KSerializer<T>
) : Serializer<T> {

    override fun serialize(topic: String, data: T): ByteArray = json.encodeToString(serializer, data).toByteArray()
}

class JsonDeserializer<T>(
    private val serializer: KSerializer<T>
) : Deserializer<T> {

    override fun deserialize(topic: String, bytes: ByteArray?): T? =
        bytes?.let { json.decodeFromString(serializer, String(it)) }
}
