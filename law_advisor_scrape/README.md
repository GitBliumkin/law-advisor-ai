# 🏛️ Law Advisor Scrapper

A Python-based system for extracting, structuring, and formatting Canadian employment and labor legislation. Supports CLI, REST API, Kafka events, and asynchronous scraping.

---

## 🚀 Features

- ⚡ Asynchronous crawling with Crawl4AI
- 📦 Structured output (JSON + Markdown)
- 🖥️ CLI for law-by-law scraping
- 🌐 REST API (FastAPI) with Kafka integration
- 🐳 Docker-compatible
- 🧪 Unit tested

---

## 📁 Project Structure

```
legislative_crawler/
├── cli/                  # CLI interface
├── api/                  # FastAPI endpoints
├── crawlers/             # Province-specific crawlers
├── kafka/                # Kafka consumer & producer
├── config/               # App + Kafka configuration
├── resources/            # links.json with legislation metadata
├── utils/                # Logging, helpers
├── tests/                # Unit tests
├── scraper_lambda.py     # Kafka Lambda entrypoint
├── requirements.txt      # All dependencies
├── Dockerfile
└── README.md
```

---

## 🧰 Installation

### ✅ Prerequisites

- Python 3.10+
- Kafka (locally or via Docker)
- Docker (optional)
- Git

### 🧱 Local Setup

```bash
git clone https://github.com/yourusername/legislative-crawler.git
cd legislative-crawler

# Create and activate virtual env
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Install Playwright browsers
python -m playwright install
```

---

## 🔄 Usage

### 🖥 CLI Mode

```bash
# Crawl a specific law
python -m legislative_crawler.main FED_CLC

# Crawl all laws in a province
python -m legislative_crawler.main ON
```

### 🌐 API Mode

```bash
python -m legislative_crawler.main api
```

API available at: `http://localhost:8000`

#### Endpoints

- `POST /crawl` → Start crawl
- `GET /crawl/{task_id}` → Get crawl status
- `GET /health` → Check if API is alive

---

## ⚙️ Kafka Lambda Scraper

Start a Kafka-based listener that reacts to province names and returns crawl results:

```bash
python scraper_lambda.py
```

**Send province names** (e.g., `ontario`, `ON`, `fed`) to topic: `scraper-requests`.

**Responses** are sent to topic: `scraper-responses`.

---

## 🐋 Docker Setup

```bash
docker build -t legislative-crawler .
docker run -p 8000:8000 legislative-crawler
```

---

## ⚙️ Configuration

Available via environment variables:

| Variable | Purpose |
|----------|---------|
| `KAFKA_BOOTSTRAP_SERVERS` | e.g. `localhost:9092` |
| `KAFKA_REQUEST_TOPIC`     | Kafka input topic |
| `KAFKA_RESPONSE_TOPIC`    | Kafka output topic |
| `KAFKA_CONSUMER_GROUP`    | Kafka group ID |
| `LINKS_FILE`              | Path to links.json |
| `API_HOST`, `API_PORT`, `API_RELOAD` | FastAPI settings |

---

## 🧪 Testing

```bash
pytest tests/
```

---

## ➕ Add New Province

1. Add alias in `CrawlerRouter.PROVINCE_ALIAS`
2. Implement a crawler in `crawlers/{province}.py`
3. Add laws to `resources/links.json`

---

## 📄 Output Format

### JSON

```json
{
  "province": "ON",
  "status": "success",
  "pages": [
    {
      "identifier": "ON_ESA",
      "markdown": "...",
      "status": "success"
    }
  ]
}
```

### Markdown

Markdown is extracted, pruned, and formatted per law page.

---

## 🧹 Troubleshooting

| Problem | Solution |
|--------|----------|
| `BrowserType.launch` error | Run `python -m playwright install` |
| Kafka group error | Ensure Kafka is running locally |
| Province not found | Normalize to uppercase / alias format |
| Empty responses | Check `links.json` and topic messages |

---

## 📜 License

MIT License – see `LICENSE` file.

---

Let me know if you want a `.env` file template or Docker Compose setup included!