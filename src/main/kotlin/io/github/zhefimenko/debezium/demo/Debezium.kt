package io.github.zhefimenko.debezium.demo

import io.github.zhefimenko.debezium.demo.utils.JsonDeserializer
import io.github.zhefimenko.debezium.demo.utils.JsonSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KeyValueMapper
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.ValueJoiner
import org.apache.kafka.streams.state.KeyValueStore
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
data class DebeziumMessage<T>(
    val op: String,
    val before: T?,
    val after: T?
) {

    @Serializable
    data class SportState(
        val id: Long,
        val code: String,
        val status: String
    )

    @Serializable
    data class CategoryState(
        val id: Long,
        val code: String,
        val status: String,
        @SerialName("sport_id")
        val sportId: Int
    )

    @Serializable
    data class CategoryKey(
        val id: Long,
        @SerialName("sport_id")
        val sportId: Int
    )

    companion object {
        val DEBEZIUM_SPORT_SERDE: Serde<DebeziumMessage<SportState>> =
            Serdes.serdeFrom(
                JsonSerializer(serializer(SportState.serializer())),
                JsonDeserializer(serializer(SportState.serializer()))
            )
        val DEBEZIUM_CATEGORY_SERDE: Serde<DebeziumMessage<CategoryState>> =
            Serdes.serdeFrom(
                JsonSerializer(serializer(CategoryState.serializer())),
                JsonDeserializer(serializer(CategoryState.serializer()))
            )
        val DEBEZIUM_CATEGORY_KEY_SERDE: Serde<CategoryKey> =
            Serdes.serdeFrom(
                JsonSerializer(CategoryKey.serializer()),
                JsonDeserializer(CategoryKey.serializer())
            )
    }
}

data class Category(
    val id: Long,
    val code: String,
    val status: String,
    val sport: Sport
) {
    constructor(sportState: DebeziumMessage.SportState, categoryState: DebeziumMessage.CategoryState)
            : this(categoryState.id, categoryState.code, categoryState.status, Sport(sportState))

    data class Sport(
        val id: Long,
        val code: String,
        val status: String
    ) {
        constructor(state: DebeziumMessage.SportState) : this(state.id, state.code, state.status)
    }
}

/**
 * 3) BUSINESS LOGIC
 */
val KAFKA_STREAM_BUILDER = StreamsBuilder()
    .also {
        val store =
            Materialized.`as`<String, DebeziumMessage<DebeziumMessage.SportState>, KeyValueStore<Bytes, ByteArray>>("sport-store")
        val valueJoiner =
            ValueJoiner<DebeziumMessage<DebeziumMessage.CategoryState>?, DebeziumMessage<DebeziumMessage.SportState>?, Category?> { leftValue, rightValue ->
                rightValue?.after?.let { sport -> leftValue?.after?.let { category -> Category(sport, category) } }
            }
        val keyValueMapper: KeyValueMapper<DebeziumMessage.CategoryKey, DebeziumMessage<DebeziumMessage.CategoryState>?, String> =
            KeyValueMapper { categoryKey, _ -> categoryKey.sportId.toString() }

        val sportTable = it.globalTable(
            "pgsql.demo.custom.sport",
            Consumed.with(Serdes.String(), DebeziumMessage.DEBEZIUM_SPORT_SERDE),
            store
        )

        it.stream(
            "pgsql.demo.custom.category",
            Consumed.with(DebeziumMessage.DEBEZIUM_CATEGORY_KEY_SERDE, DebeziumMessage.DEBEZIUM_CATEGORY_SERDE)
        )
            .leftJoin(sportTable, keyValueMapper, valueJoiner)
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
