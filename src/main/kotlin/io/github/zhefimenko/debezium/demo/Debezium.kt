package io.github.zhefimenko.debezium.demo

import io.github.zhefimenko.debezium.demo.utils.JsonDeserializer
import io.github.zhefimenko.debezium.demo.utils.JsonSerializer
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import java.util.*

/**
 * @author Yauheni Yefimenka
 */

private val log = KotlinLogging.logger {}

/**
 * 1) CONFIGURATION
 */
val PROPERTIES = Properties()
    .also {
        it[StreamsConfig.APPLICATION_ID_CONFIG] = "demo-debezium-group"
        it[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        it[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        it[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
    }

/**
 * 2) DOMAIN
 */
@Serializable
data class DebeziumMessage(
    val op: String,
    val before: SportState?,
    val after: SportState?
) {

    @Serializable
    data class SportState(
        val id: Long,
        val code: String,
        val status: String
    )

    companion object {
        val DEBEZIUM_SERDE: Serde<DebeziumMessage> =
            Serdes.serdeFrom(JsonSerializer(serializer()), JsonDeserializer(serializer()))
    }
}

/**
 * 3) BUSINESS LOGIC
 */
val KAFKA_STREAM_BUILDER = StreamsBuilder()
    .also {
        it.stream("pgsql.demo.custom.sport", Consumed.with(Serdes.String(), DebeziumMessage.DEBEZIUM_SERDE))
            .foreach { key, value -> System.err.println("$key: $value") }
    }

/**
 * 4) START
 */
fun main() {
    val streams = KafkaStreams(KAFKA_STREAM_BUILDER.build(), PROPERTIES)
    streams.start()

    // Add shutdown hook to stop the Kafka Streams threads.
    // You can optionally provide a timeout to `close`.
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
