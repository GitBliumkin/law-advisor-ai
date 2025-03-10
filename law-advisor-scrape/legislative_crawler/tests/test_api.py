"""
Unit tests for the API endpoints.
"""
import pytest
from unittest.mock import patch, AsyncMock
from fastapi.testclient import TestClient

from legislative_crawler.api.endpoints import app, crawl_task

client = TestClient(app)

def test_health_check():
    """Test health check endpoint."""
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "healthy"}

@patch("legislative_crawler.api.endpoints.get_link_by_identifier")
def test_crawl_endpoint_with_law_identifier(mock_get_link):
    """Test crawl endpoint with a law identifier."""
    mock_get_link.return_value = {
        "identifier": "FED_CLC",
        "province": "FED",
        "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
        "law_name": "Canada Labour Code"
    }
    
    response = client.post("/crawl", json={"identifier": "FED_CLC"})
    
    assert response.status_code == 200
    assert "task_id" in response.json()
    assert response.json()["status"] == "pending"
    assert "FED_CLC" in response.json()["message"]

@patch("legislative_crawler.api.endpoints.get_links_by_province")
def test_crawl_endpoint_with_province(mock_get_links):
    """Test crawl endpoint with a province code."""
    mock_get_links.return_value = [
        {
            "identifier": "FED_CLC",
            "province": "FED",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
            "law_name": "Canada Labour Code"
        },
        {
            "identifier": "FED_CHRA",
            "province": "FED",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/h-6/",
            "law_name": "Canadian Human Rights Act"
        }
    ]
    
    response = client.post("/crawl", json={"identifier": "FED"})
    
    assert response.status_code == 200
    assert "task_id" in response.json()
    assert response.json()["status"] == "pending"
    assert "FED" in response.json()["message"]

@patch("legislative_crawler.api.endpoints.get_link_by_identifier")
def test_crawl_endpoint_with_unknown_identifier(mock_get_link):
    """Test crawl endpoint with an unknown identifier."""
    mock_get_link.return_value = None
    
    response = client.post("/crawl", json={"identifier": "UNKNOWN"})
    
    assert response.status_code == 404
    assert "Unknown identifier" in response.json()["detail"]

@patch("legislative_crawler.api.endpoints.get_links_by_province")
def test_crawl_endpoint_with_unknown_province(mock_get_links):
    """Test crawl endpoint with an unknown province."""
    mock_get_links.return_value = []
    
    response = client.post("/crawl", json={"identifier": "XYZ"})
    
    assert response.status_code == 404
    assert "No links found for province" in response.json()["detail"]

def test_get_task_status():
    """Test get_task_status endpoint."""
    response = client.get("/crawl/test-task-id")
    
    assert response.status_code == 200
    assert response.json()["task_id"] == "test-task-id"
    assert "status" in response.json()

@pytest.mark.asyncio
@patch("legislative_crawler.api.endpoints.crawl_identifier")
@patch("legislative_crawler.api.endpoints.send_to_kafka")
async def test_crawl_task_with_law_identifier(mock_send_to_kafka, mock_crawl_identifier):
    """Test crawl_task function with a law identifier."""
    mock_crawl_identifier.return_value = {
        "url_identifier": "FED_CLC",
        "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
        "law_name": "Canada Labour Code",
        "scrape_date": "01/01/2024",
        "scrape_time": "12:00",
        "status": "success",
        "markdown": "# Canada Labour Code"
    }
    mock_send_to_kafka.return_value = None
    
    await crawl_task("FED_CLC", "test-task-id")
    
    # Check that send_to_kafka was called twice
    assert mock_send_to_kafka.call_count == 2
    
    # Check first call (result)
    args1, _ = mock_send_to_kafka.call_args_list[0]
    assert "fed_clc" in args1[0]
    assert args1[1]["task_id"] == "test-task-id"
    assert args1[1]["result"]["url_identifier"] == "FED_CLC"
    
    # Check second call (completion)
    args2, _ = mock_send_to_kafka.call_args_list[1]
    assert "tasks" in args2[0]
    assert args2[1]["task_id"] == "test-task-id"
    assert args2[1]["status"] == "completed"

@pytest.mark.asyncio
@patch("legislative_crawler.api.endpoints.crawl_province")
@patch("legislative_crawler.api.endpoints.send_to_kafka")
async def test_crawl_task_with_province(mock_send_to_kafka, mock_crawl_province):
    """Test crawl_task function with a province code."""
    mock_crawl_province.return_value = [
        {
            "url_identifier": "FED_CLC",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
            "law_name": "Canada Labour Code",
            "scrape_date": "01/01/2024",
            "scrape_time": "12:00",
            "status": "success",
            "markdown": "# Canada Labour Code"
        },
        {
            "url_identifier": "FED_CHRA",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/h-6/",
            "law_name": "Canadian Human Rights Act",
            "scrape_date": "01/01/2024",
            "scrape_time": "12:00",
            "status": "success",
            "markdown": "# Canadian Human Rights Act"
        }
    ]
    mock_send_to_kafka.return_value = None
    
    await crawl_task("FED", "test-task-id")
    
    # Check that send_to_kafka was called 3 times (2 results + 1 completion)
    assert mock_send_to_kafka.call_count == 3
    
    # Check last call (completion)
    args, _ = mock_send_to_kafka.call_args_list[2]
    assert "tasks" in args[0]
    assert args[1]["task_id"] == "test-task-id"
    assert args[1]["status"] == "completed"
    assert "2 laws" in args[1]["message"]
