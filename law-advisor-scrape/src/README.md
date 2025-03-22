# Legislative Crawler

A Python-based web-crawling system for extracting, structuring, and formatting legislative texts related to employment and labor regulations across Canada.

## Features

- Asynchronous crawling capabilities using Crawl4AI
- Structured data output in JSON and Markdown
- Command-Line Interface (CLI) for on-demand operations
- REST API using FastAPI with Kafka integration for event-driven workflows
- Docker deployment for easy containerization

## Project Structure

```
legislative_crawler/
├── links.json              # Dynamic list of URLs
├── main.py                 # Central coordinator for crawling tasks
├── config/
│   ├── __init__.py
│   └── crawl_config.py     # Central configuration logic
├── crawlers/
│   ├── __init__.py
│   ├── fed.py              # Federal law-specific scaffolding
│   ├── on.py               # Ontario law-specific scaffolding
│   ├── bc.py               # BC law-specific scaffolding
├── api/
│   ├── __init__.py
│   └── endpoints.py        # FastAPI endpoints & Kafka integration
├── cli/
│   ├── __init__.py
│   └── interface.py        # CLI command-handling
├── utils/
│   ├── __init__.py
│   └── helpers.py          # Logging, error handling, common utilities
├── tests/
│   ├── __init__.py
│   ├── test_crawl.py       # Unit tests for crawling
│   ├── test_api.py         # Unit tests for API endpoints
│   └── test_cli.py         # Unit tests for CLI
├── Dockerfile              # Docker container setup
├── requirements.txt        # Python dependencies
└── README.md               # Documentation
```

## Installation

### Prerequisites

- Python 3.10 or higher
- pip (Python package manager)
- Docker (optional, for containerized deployment)
- Kafka (optional, for event-driven workflows)

### Local Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/legislative-crawler.git
cd legislative-crawler
```

2. Create a virtual environment:

```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install dependencies:

```bash
pip install -r legislative_crawler/requirements.txt
```

## Usage

### Command-Line Interface (CLI)

The CLI allows you to crawl a specific law or all laws for a province:

```bash
# Crawl a specific law
python -m legislative_crawler.main FED_CLC --output-json fed_clc.json --output-md fed_clc.md

# Crawl all laws for a province
python -m legislative_crawler.main FED --output-json fed.json --output-md fed.md
```

### API Server

Start the API server:

```bash
python -m legislative_crawler.main api
```

The API will be available at http://localhost:8000.

#### API Endpoints

- `POST /crawl`: Start a crawling task
  - Request body: `{"identifier": "FED_CLC"}`
  - Response: `{"task_id": "...", "status": "pending", "message": "..."}`

- `GET /crawl/{task_id}`: Get task status
  - Response: `{"task_id": "...", "status": "...", "message": "..."}`

- `GET /health`: Health check
  - Response: `{"status": "healthy"}`

### Docker Deployment

1. Build the Docker image:

```bash
docker build -t legislative-crawler -f legislative_crawler/Dockerfile legislative_crawler
```

2. Run the container:

```bash
# Run the API server
docker run -p 8000:8000 legislative-crawler

# Run the CLI
docker run --rm legislative-crawler python -m legislative_crawler.main FED --output-json /app/output/fed.json --output-md /app/output/fed.md
```

## Configuration

The application can be configured using environment variables:

- `LINKS_FILE`: Path to the links.json file
- `OUTPUT_DIR`: Path to the output directory
- `MAX_RETRIES`: Maximum number of retries for failed requests
- `TIMEOUT`: Request timeout in seconds
- `CONCURRENT_REQUESTS`: Maximum number of concurrent requests
- `USER_AGENT`: User agent string for HTTP requests
- `API_HOST`: API server host
- `API_PORT`: API server port
- `API_WORKERS`: Number of API server workers
- `API_RELOAD`: Enable/disable auto-reload for the API server
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers
- `KAFKA_TOPIC`: Kafka topic
- `KAFKA_GROUP_ID`: Kafka consumer group ID

## URL Structure

The application uses a list-based JSON file (`links.json`) to store URLs and metadata:

```json
[
  {
    "province": "FED",
    "identifier": "FED_CLC",
    "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
    "law_name": "Canada Labour Code",
    "last_updated": "2024-01-15",
    "source": "Government of Canada"
  },
  ...
]
```

## Output Format

### JSON Output

```json
{
  "url_identifier": "FED_CLC",
  "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
  "law_name": "Canada Labour Code",
  "scrape_date": "DD/MM/YYYY",
  "scrape_time": "HH:MM",
  "status": "success",
  "markdown": "<actual markdown content>"
}
```

### Markdown Output

The Markdown output contains the structured content of the law, formatted for easy reading and ingestion by LLMs or vector databases.

## Error Handling and Troubleshooting

The application uses a centralized logging system to track errors and provide debugging information. Logs are output to the console by default.

Common issues:

- **Connection errors**: Check your internet connection and the URL in links.json
- **Kafka connection errors**: Ensure Kafka is running and accessible
- **Permission errors**: Check file permissions for output files

## Development

### Running Tests

```bash
pytest legislative_crawler/tests/
```

### Adding a New Province

1. Add the province to `PROVINCE_SETTINGS` in `config/crawl_config.py`
2. Create a new crawler file in `crawlers/`
3. Add URLs to `links.json`

## License

This project is licensed under the MIT License - see the LICENSE file for details.
