"""
Configuration settings for the legislative crawler.
"""
import os
import json
from pathlib import Path
from typing import Dict, List, Optional, Any

# Base directory
BASE_DIR = Path(__file__).parent.parent.absolute()

# Links file path
LINKS_FILE = Path(__file__).parent.parent / "resources/links.json"

# Output directory
OUTPUT_DIR = os.environ.get("OUTPUT_DIR", str(BASE_DIR / "output"))

# Crawler settings
CRAWLER_SETTINGS = {
    "max_retries": int(os.environ.get("MAX_RETRIES", 3)),
    "timeout": int(os.environ.get("TIMEOUT", 30)),
    "concurrent_requests": int(os.environ.get("CONCURRENT_REQUESTS", 5)),
    "user_agent": os.environ.get(
        "USER_AGENT", 
        "Legislative Crawler/1.0 (+https://example.com/bot)"
    ),
}

# API settings
API_SETTINGS = {
    "host": os.environ.get("API_HOST", "0.0.0.0"),
    "port": int(os.environ.get("API_PORT", 8000)),
    "workers": int(os.environ.get("API_WORKERS", 4)),
}

# Kafka settings
KAFKA_SETTINGS = {
    "bootstrap_servers": os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
    "request_topic": os.environ.get("KAFKA_REQUEST_TOPIC", "scraper-requests"),
    "response_topic": os.environ.get("KAFKA_RESPONSE_TOPIC", "scraper-responses"),
    "group_id": os.getenv("KAFKA_CONSUMER_GROUP", "scraper-group")
}

# Province-specific settings
PROVINCE_SETTINGS = {
    "FED": {
        "name": "Federal",
        "crawler_module": "crawlers.fed",
        "crawler_class": "FederalCrawler",
    },
    "ONT": {
        "name": "Ontario",
        "crawler_module": "crawlers.on",
        "crawler_class": "OntarioCrawler",
    },
    "BC": {
        "name": "British Columbia",
        "crawler_module": "crawlers.bc",
        "crawler_class": "BritishColumbiaCrawler",
    },
}

def get_links() -> List[Dict[str, Any]]:
    """
    Load links from the links.json file.
    
    Returns:
        List[Dict[str, Any]]: List of link dictionaries
    """
    try:
        with open(LINKS_FILE, "r") as f:
            return json.load(f)
    except (FileNotFoundError, json.JSONDecodeError) as e:
        raise Exception(f"Error loading links file: {e}")

def get_link_by_identifier(identifier: str) -> Optional[Dict[str, Any]]:
    """
    Get a link by its identifier.
    
    Args:
        identifier (str): Link identifier (e.g., FED_CLC)
        
    Returns:
        Optional[Dict[str, Any]]: Link dictionary or None if not found
    """
    links = get_links()
    for link in links:
        if link["identifier"] == identifier:
            return link
    return None

def get_links_by_province(province: str) -> List[Dict[str, Any]]:
    """
    Get all links for a specific province.
    
    Args:
        province (str): Province code (e.g., FED, ONT)
        
    Returns:
        List[Dict[str, Any]]: List of link dictionaries for the province
    """
    links = get_links()
    return [link for link in links if link["province"] == province]

def get_province_settings(province: str) -> Dict[str, Any]:
    """
    Get settings for a specific province.
    
    Args:
        province (str): Province code (e.g., FED, ONT)
        
    Returns:
        Dict[str, Any]: Province settings
    """
    if province not in PROVINCE_SETTINGS:
        raise ValueError(f"Unknown province: {province}")
    return PROVINCE_SETTINGS[province]
