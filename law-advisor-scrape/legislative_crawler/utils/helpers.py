"""
Utility functions for the legislative crawler.
"""
import os
import json
import logging
import datetime
from pathlib import Path
from typing import Dict, Any, Optional, Union

# Configure logging
def setup_logging(level: int = logging.INFO) -> logging.Logger:
    """
    Set up logging configuration.
    
    Args:
        level (int): Logging level
        
    Returns:
        logging.Logger: Configured logger
    """
    logger = logging.getLogger("legislative_crawler")
    logger.setLevel(level)
    
    # Create console handler
    console_handler = logging.StreamHandler()
    console_handler.setLevel(level)
    
    # Create formatter
    formatter = logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    )
    console_handler.setFormatter(formatter)
    
    # Add handler to logger
    logger.addHandler(console_handler)
    
    return logger

# Create logger instance
logger = setup_logging()

def save_to_json(data: Dict[str, Any], filename: str) -> str:
    """
    Save data to a JSON file.
    
    Args:
        data (Dict[str, Any]): Data to save
        filename (str): Output filename
        
    Returns:
        str: Path to the saved file
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(os.path.abspath(filename)), exist_ok=True)
        
        with open(filename, "w") as f:
            json.dump(data, f, indent=2)
        
        logger.info(f"Data saved to {filename}")
        return filename
    except Exception as e:
        logger.error(f"Error saving data to {filename}: {e}")
        raise

def save_to_markdown(content: str, filename: str) -> str:
    """
    Save content to a Markdown file.
    
    Args:
        content (str): Markdown content
        filename (str): Output filename
        
    Returns:
        str: Path to the saved file
    """
    try:
        # Create directory if it doesn't exist
        os.makedirs(os.path.dirname(os.path.abspath(filename)), exist_ok=True)
        
        with open(filename, "w") as f:
            f.write(content)
        
        logger.info(f"Content saved to {filename}")
        return filename
    except Exception as e:
        logger.error(f"Error saving content to {filename}: {e}")
        raise

def get_current_datetime() -> Dict[str, str]:
    """
    Get current date and time in formatted strings.
    
    Returns:
        Dict[str, str]: Dictionary with date and time
    """
    now = datetime.datetime.now()
    return {
        "date": now.strftime("%d/%m/%Y"),
        "time": now.strftime("%H:%M"),
        "iso": now.isoformat()
    }

def format_result(
    identifier: str, 
    url: str, 
    law_name: str, 
    markdown_content: str, 
    status: str = "success"
) -> Dict[str, Any]:
    """
    Format crawling result into a standardized dictionary.
    
    Args:
        identifier (str): URL identifier
        url (str): Source URL
        law_name (str): Law name
        markdown_content (str): Markdown content
        status (str): Crawling status
        
    Returns:
        Dict[str, Any]: Formatted result
    """
    datetime_info = get_current_datetime()
    
    return {
        "url_identifier": identifier,
        "url": url,
        "law_name": law_name,
        "scrape_date": datetime_info["date"],
        "scrape_time": datetime_info["time"],
        "status": status,
        "markdown": markdown_content
    }

def handle_error(
    identifier: str, 
    url: str, 
    law_name: str, 
    error: Exception
) -> Dict[str, Any]:
    """
    Handle crawling error and format into a standardized dictionary.
    
    Args:
        identifier (str): URL identifier
        url (str): Source URL
        law_name (str): Law name
        error (Exception): Error that occurred
        
    Returns:
        Dict[str, Any]: Formatted error result
    """
    logger.error(f"Error crawling {identifier} ({url}): {error}")
    
    return format_result(
        identifier=identifier,
        url=url,
        law_name=law_name,
        markdown_content="",
        status=f"error: {str(error)}"
    )
