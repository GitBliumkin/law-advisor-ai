#!/bin/bash

# Define a function to display messages
log() {
  echo "[local-stack] $1"
}

log "Starting environment setup..."

# Pull the latest images (optional)
log "Pulling latest images..."
docker-compose pull

# Build and start containers in detached mode
log "Building and starting Docker containers..."
docker-compose up --build -d

# Wait for services to become healthy (optional)
log "Waiting for containers to become healthy..."
docker-compose ps

log "Environment setup complete. Containers are running in the background."
