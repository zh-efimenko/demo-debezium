#!/bin/bash

topics=(
  pgsql.demo.marketing.user
  pgsql.demo.custom.sport
  pgsql.demo.custom.category
  mysql.demo.demo-debezium.users
  mysql.demo.demo-debezium.sport
  mysql.demo.demo-debezium.category
)

for topic in "${topics[@]}"; do
  kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic "$topic"
  execResult=$?

  if [ $execResult -ne 0 ]; then
    echo "Topic creation error: $topic. Error code: $execResult"
    exit $execResult
  fi
done

echo "All topics were successfully created!"
