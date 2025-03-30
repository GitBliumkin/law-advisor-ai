#!/bin/bash

set -e

KAFKA_CONTAINER_NAME=lawadvisor-kafka
KAFKA_TOPICS=("scraper-requests" "scraper-responses")

echo "🚀 Booting up Law Advisor local environment..."

# Step 1: Start containers from docker-compose
echo "📦 Starting containers with Docker Compose..."
docker compose up -d

# Step 2: Wait for Kafka to be ready
echo "⏳ Waiting for Kafka to initialize..."
sleep 10  # You can improve this with a healthcheck

# Step 3: Create Kafka topics for scraper communication
echo "🔧 Creating Kafka topics for scraper integration..."
for topic in "${KAFKA_TOPICS[@]}"; do
  echo "➡️ Creating topic: $topic"
  docker exec $KAFKA_CONTAINER_NAME rpk topic create "$topic" || echo "⚠️ Topic $topic may already exist"
done

echo "✅ Local environment is ready with scraper topics configured!"
