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
# Using 'psql' ensures that the SQL commands in init.sql are executed.
log "Initializing databases..."
docker exec -i postgres psql -U user -f /docker-entrypoint-initdb.d/init.sql

# Display running containers
log "Checking container statuses..."
docker-compose ps

log "Environment setup complete. Containers are running in the background."
