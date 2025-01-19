#!/bin/bash

# Параметры Kafka
BOOTSTRAP_SERVER="127.0.0.1:9092"
TOPIC="pgsql.demo.kafka.signal"

# Сообщение
KEY="pgsql.demo"
VALUE='{"type":"execute-snapshot", "data": {"data-collections": ["custom.category"], "type": "incremental"}}'

# Отправка сообщения в Kafka
echo "${KEY}:${VALUE}" | kafka-console-producer \
  --bootstrap-server "${BOOTSTRAP_SERVER}" \
  --topic "${TOPIC}" \
  --property "parse.key=true" \
  --property "key.separator=:"

echo "Сообщение отправлено в Kafka топик '${TOPIC}'"
