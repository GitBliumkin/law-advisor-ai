#!/bin/bash

set -e

KAFKA_CONTAINER_NAME=lawadvisor-kafka
KAFKA_TOPICS=("scraper-requests" "scraper-responses")

echo "Starting Law Advisor local environment..."

# Step 1: Start containers from docker-compose
echo "Starting containers with Docker Compose..."
docker compose up -d

# Step 2: Wait for Kafka to initialize
echo "Waiting for Kafka to initialize..."
sleep 10  # You can replace this with a proper healthcheck loop if needed

# Step 3: Create Kafka topics for scraper communication
echo "Creating Kafka topics for scraper integration..."
for topic in "${KAFKA_TOPICS[@]}"; do
  echo "Creating topic: $topic"
  docker exec $KAFKA_CONTAINER_NAME kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --topic "$topic" \
    --partitions 1 \
    --replication-factor 1 || echo "Topic $topic may already exist"
done

echo "Local environment is ready with scraper topics configured."
