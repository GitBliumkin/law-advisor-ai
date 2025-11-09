#!/bin/bash

echo "Installing Python dependencies..."
pip install -r requirements.txt

echo "Installing Playwright browsers..."
python -m playwright install

echo "Installing Ebedder SentenceTransformer..."
pip install sentence-transformers crawl4ai typer rich

echo "Installing Testing Library"
pip install pytest typer[all] pytest-asyncio

echo "Setup complete. You're ready to run the crawler!"
