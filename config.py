import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()

BASE_DIR = Path(__file__).resolve().parent
DATA_DIR = BASE_DIR / "data"
RAW_DIR = DATA_DIR / "raw"
OUTPUT_DIR = DATA_DIR / "outputs"
RAW_DIR.mkdir(parents=True, exist_ok=True)
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# Crawler
# Default to CanLII ESA 2000 page
START_URL = "https://www.canlii.org/en/on/laws/stat/so-2000-c-41/latest/so-2000-c-41.html"
USER_AGENT = os.getenv("USER_AGENT", "HR-Compliance-Bot/1.0")
CONCURRENCY = int(os.getenv("CRAWL_CONCURRENCY", 4))

# Embeddings / LLM
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
EMBEDDING_MODEL_NAME = "text-embedding-3-small"  # customise as needed

# Qdrant
QDRANT_URL = os.getenv("QDRANT_URL", "http://localhost:6333")
QDRANT_API_KEY = os.getenv("QDRANT_API_KEY")
COLLECTION_NAME = "esa2000_chunks"
VECTOR_DIM = 1536  # depends on embedding model

# Dry-run & dumping
DRY_RUN = os.getenv("DRY_RUN", "false").lower() == "true"
INCLUDE_EMBEDDINGS = os.getenv("INCLUDE_EMBEDDINGS", "true").lower() == "true" 