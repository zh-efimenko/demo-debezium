FROM prom/prometheus:v2.43.0

RUN sed -i -e "s/\"localhost:9090\"/\"localhost:9090\",\"debezium:8080\"/" /etc/prometheus/prometheus.yml
