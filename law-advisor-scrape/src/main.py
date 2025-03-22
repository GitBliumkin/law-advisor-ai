#!/usr/bin/env python3
"""
Legislative Crawler - Main entry point.

This module serves as the main entry point for the legislative crawler.
It can be used to run the crawler from the command line or to start the API server.
"""
import os
import sys
import uvicorn
from pathlib import Path

# Add the parent directory to sys.path
sys.path.insert(0, str(Path(__file__).parent.parent))

from legislative_crawler.cli.interface import app as cli_app
from legislative_crawler.config.crawl_config import API_SETTINGS
from legislative_crawler.utils.helpers import logger

def run_api():
    """Run the FastAPI server."""
    from legislative_crawler.api.endpoints import app as api_app
    
    logger.info(f"Starting API server on {API_SETTINGS['host']}:{API_SETTINGS['port']}")
    uvicorn.run(
        "legislative_crawler.api.endpoints:app",
        host=API_SETTINGS["host"],
        port=API_SETTINGS["port"],
        workers=API_SETTINGS["workers"],
        reload=os.environ.get("API_RELOAD", "false").lower() == "true"
    )

if __name__ == "__main__":
    # Check if the first argument is "api"
    if len(sys.argv) > 1 and sys.argv[1] == "api":
        # Remove "api" from sys.argv
        sys.argv.pop(1)
        run_api()
    else:
        # Run the CLI
        cli_app()
