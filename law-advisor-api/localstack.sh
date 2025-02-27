#!/bin/bash

# Define a function to display messages with timestamps
log() {
  echo "[local-stack] $(date +'%Y-%m-%d %H:%M:%S') - $1"
}

log "Stopping and removing any existing containers and volumes..."
docker-compose down -v

log "Starting environment setup..."

# Pull the latest images (optional)
log "Pulling latest images..."
docker-compose pull

# Build and start containers in detached mode
log "Building and starting Docker containers..."
docker-compose up --build -d

# Function to check PostgreSQL readiness
check_postgres() {
  log "Checking if PostgreSQL is ready..."
  until docker exec postgres pg_isready -U user; do
    log "Waiting for PostgreSQL to be ready..."
    sleep 2
  done
  log "PostgreSQL is ready!"
}

# Wait for PostgreSQL to initialize and be ready
check_postgres

# Manually run the initialization SQL script
log "Initializing databases..."
docker exec -i postgres psql -U user -f /docker-entrypoint-initdb.d/init.sql

# Function to check Kafka readiness
check_kafka() {
  log "Checking if Kafka is ready..."
  until docker exec kafka kafka-topics --list --bootstrap-server localhost:9092 >/dev/null 2>&1; do
    log "Waiting for Kafka to be ready..."
    sleep 5
  done
  log "Kafka is ready!"
}

# Wait for Kafka to initialize and be ready
check_kafka

# Define Kafka topics
TOPICS=("scraper_requests" "scraper_responses" "scraper_errors")

# Create Kafka topics if they don't exist
for topic in "${TOPICS[@]}"; do
  log "Ensuring Kafka topic exists: $topic"
  docker exec kafka kafka-topics --create --topic "$topic" --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --if-not-exists
done

# Display running containers
log "Checking container statuses..."
docker-compose ps

log "Environment setup complete. Containers are running in the background."
