# Retrieval-Augmented Generation (RAG) Pipeline – ESA 2000 Act

This repository contains a **deployment-ready end-to-end pipeline** that:

1. **Crawls** the Ontario Employment Standards Act, 2000 (ESA 2000) from
   <https://www.ontario.ca/laws/statute/00e41> using **crawl4ai**.
2. **Parses & chunks** the statute into a hierarchy (Parts → Sections → Sub-sections)
   with stable IDs using **BeautifulSoup** and **LlamaIndex**.
3. **Embeds** each chunk via **LangChain** + **OpenAI** embeddings.
4. **Stores** vectors and rich metadata in **Qdrant** for hybrid
   (metadata + semantic) retrieval.

---

## Quick Start
```bash
# 1) Create venv & install
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt

# 2) Configure environment
cp env.example .env
# -> add OPENAI_API_KEY. If you don't have Qdrant yet, leave QDRANT_* blank
#    and set DRY_RUN=true.

# 3a) DRY RUN (no Qdrant): crawl → chunk → (optional) embed → dump JSONL
python main.py --dry-run --include-embeddings
# Outputs to data/outputs/nodes.jsonl

# 3b) Full pipeline (requires Qdrant running locally or remote)
python main.py

# 4) Convert JSONL to single JSON file (optional)
python utils/convert_jsonl_to_json.py

# 5) Inspect dump
python - <<'PY'
import json
from pathlib import Path
with open('data/outputs/nodes.json') as f:
    data = json.load(f)
    print(f"Total nodes: {data['metadata']['total_nodes']}")
    for i, node in enumerate(data['nodes'][:3]):
        print(f"{i}: {node['id'][:8]} | {node['metadata'].get('title', 'N/A')} | {len(node['text'])} chars")
PY
```

On completion you will have either a JSONL dump (`data/outputs/nodes.jsonl`) and/or a single JSON file (`data/outputs/nodes.json`) or a populated Qdrant collection named `esa2000_chunks`.

---

### Directory Layout
```
.
├── crawler/                    # crawl4ai spider & HTML downloader
│   ├── __init__.py
│   └── crawler.py
├── processors/                 # chunker, embedder, index builder
│   ├── __init__.py
│   ├── chunker.py
│   ├── embedder.py
│   └── index_builder.py
├── utils/                      # logging / helpers / JSON dump
│   ├── __init__.py
│   ├── logging_setup.py
│   ├── dump.py
│   └── convert_jsonl_to_json.py
├── config.py                   # centralized settings & DRY_RUN flags
├── main.py                     # orchestration script (CLI flags)
├── query_demo.py               # sample similarity search
├── requirements.txt
├── env.example
├── on.py                       # original working crawler
└── data/                       # generated data
    ├── raw/                    # downloaded HTML files
    └── outputs/                # processed output (JSONL/JSON)
```

---

### CLI Flags

The `main.py` script supports the following command-line flags:

- `--dry-run` – Skip Qdrant operations and write JSONL dump to `data/outputs/nodes.jsonl`
- `--no-embed` – Skip embedding computation (faster; `embedding` field will be null in output)
- `--include-embeddings` – Include embedding vectors in JSONL output (default: true; makes file larger)

### Environment Variables

Key configuration options in `.env`:

```bash
# Required for embeddings
OPENAI_API_KEY=your_openai_key_here

# Qdrant settings (optional for dry-run)
QDRANT_URL=http://localhost:6333
QDRANT_API_KEY=your_qdrant_key

# Pipeline control
DRY_RUN=true                    # Skip Qdrant, output JSONL only
INCLUDE_EMBEDDINGS=true         # Include vectors in JSONL output
CRAWL_CONCURRENCY=4            # Number of concurrent crawler requests
```

---

### Output Formats

The pipeline generates two output formats:

1. **JSONL** (`data/outputs/nodes.jsonl`) - One JSON object per line, suitable for streaming
2. **Single JSON** (`data/outputs/nodes.json`) - All nodes in a single array with metadata

To convert between formats:
```bash
# JSONL → Single JSON
python utils/convert_jsonl_to_json.py

# Custom paths
python utils/convert_jsonl_to_json.py --input custom.jsonl --output custom.json
```

---

### Notes
* **Concurrency** – crawler runs async with throttling (respects `robots.txt`).
* **Resumability** – downloaded HTML is cached to `data/raw`; re-running skips already-fetched pages.
* **Fault-tolerance** – broken pages logged, pipeline continues.
* **Hierarchy** – every chunk stores `level`, `section`, and `parent_id` for context expansion.
* **ESA-specific parsing** – optimized for Ontario ESA 2000 HTML structure with CSS classes.
* **Extensibility** – swap embedding model or vector DB via `config.py`.

---

© 2025. MIT License.
