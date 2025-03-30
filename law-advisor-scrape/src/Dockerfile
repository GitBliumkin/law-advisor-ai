FROM python:3.10-slim

WORKDIR /app

# Copy requirements first for better caching
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application
COPY . .

# Create output directory
RUN mkdir -p /app/output

# Set environment variables
ENV PYTHONPATH=/app
ENV LINKS_FILE=/app/links.json
ENV OUTPUT_DIR=/app/output
ENV API_HOST=0.0.0.0
ENV API_PORT=8000
ENV API_WORKERS=4
ENV KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ENV KAFKA_TOPIC=legislative_crawler
ENV KAFKA_GROUP_ID=legislative_crawler_group

# Expose API port
EXPOSE 8000

# Set entrypoint
ENTRYPOINT ["python", "-m", "legislative_crawler.main"]

# Default command (can be overridden)
CMD ["api"]
