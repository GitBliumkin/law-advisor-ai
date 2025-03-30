"""
Unit tests for the crawling functionality.
"""
import pytest
import asyncio
from unittest.mock import patch, MagicMock

from legislative_crawler.crawlers.fed import FederalCrawler
from legislative_crawler.crawlers.on import OntarioCrawler
from legislative_crawler.crawlers.bc import BritishColumbiaCrawler
from legislative_crawler.config.crawl_config import get_link_by_identifier, get_links_by_province

@pytest.fixture
def sample_law():
    """Sample law fixture."""
    return {
        "province": "FED",
        "identifier": "FED_CLC",
        "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
        "law_name": "Canada Labour Code",
        "last_updated": "2024-01-15",
        "source": "Government of Canada"
    }

@pytest.fixture
def sample_laws():
    """Sample laws fixture."""
    return [
        {
            "province": "FED",
            "identifier": "FED_CLC",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/L-2/",
            "law_name": "Canada Labour Code",
            "last_updated": "2024-01-15",
            "source": "Government of Canada"
        },
        {
            "province": "FED",
            "identifier": "FED_CHRA",
            "url": "https://laws-lois.justice.gc.ca/eng/acts/h-6/",
            "law_name": "Canadian Human Rights Act",
            "last_updated": "2023-12-15",
            "source": "Government of Canada"
        }
    ]

@pytest.mark.asyncio
async def test_federal_crawler_crawl_law(sample_law):
    """Test FederalCrawler.crawl_law method."""
    crawler = FederalCrawler()
    
    result = await crawler.crawl_law(
        identifier=sample_law["identifier"],
        url=sample_law["url"],
        law_name=sample_law["law_name"]
    )
    
    assert result["url_identifier"] == sample_law["identifier"]
    assert result["url"] == sample_law["url"]
    assert result["law_name"] == sample_law["law_name"]
    assert result["status"] == "success"
    assert "markdown" in result
    assert sample_law["law_name"] in result["markdown"]

@pytest.mark.asyncio
async def test_federal_crawler_crawl_all_laws(sample_laws):
    """Test FederalCrawler.crawl_all_laws method."""
    crawler = FederalCrawler()
    
    results = await crawler.crawl_all_laws(sample_laws)
    
    assert len(results) == len(sample_laws)
    for i, result in enumerate(results):
        assert result["url_identifier"] == sample_laws[i]["identifier"]
        assert result["url"] == sample_laws[i]["url"]
        assert result["law_name"] == sample_laws[i]["law_name"]
        assert result["status"] == "success"
        assert "markdown" in result
        assert sample_laws[i]["law_name"] in result["markdown"]

@pytest.mark.asyncio
async def test_ontario_crawler_crawl_law():
    """Test OntarioCrawler.crawl_law method."""
    crawler = OntarioCrawler()
    
    law = {
        "identifier": "ONT_ESA",
        "url": "https://www.ontario.ca/laws/statute/00e41",
        "law_name": "Employment Standards Act"
    }
    
    result = await crawler.crawl_law(
        identifier=law["identifier"],
        url=law["url"],
        law_name=law["law_name"]
    )
    
    assert result["url_identifier"] == law["identifier"]
    assert result["url"] == law["url"]
    assert result["law_name"] == law["law_name"]
    assert result["status"] == "success"
    assert "markdown" in result
    assert law["law_name"] in result["markdown"]

@pytest.mark.asyncio
async def test_bc_crawler_crawl_law():
    """Test BritishColumbiaCrawler.crawl_law method."""
    crawler = BritishColumbiaCrawler()
    
    law = {
        "identifier": "BC_ESA",
        "url": "https://www.bclaws.gov.bc.ca/civix/document/id/complete/statreg/00_96113_01",
        "law_name": "Employment Standards Act"
    }
    
    result = await crawler.crawl_law(
        identifier=law["identifier"],
        url=law["url"],
        law_name=law["law_name"]
    )
    
    assert result["url_identifier"] == law["identifier"]
    assert result["url"] == law["url"]
    assert result["law_name"] == law["law_name"]
    assert result["status"] == "success"
    assert "markdown" in result
    assert law["law_name"] in result["markdown"]

@pytest.mark.parametrize("identifier", ["FED_CLC", "ONT_ESA", "BC_ESA"])
def test_get_link_by_identifier(identifier):
    """Test get_link_by_identifier function."""
    with patch("legislative_crawler.config.crawl_config.get_links") as mock_get_links:
        mock_get_links.return_value = [
            {"identifier": "FED_CLC", "province": "FED"},
            {"identifier": "ONT_ESA", "province": "ONT"},
            {"identifier": "BC_ESA", "province": "BC"}
        ]
        
        link = get_link_by_identifier(identifier)
        
        assert link is not None
        assert link["identifier"] == identifier

@pytest.mark.parametrize("province,expected_count", [("FED", 2), ("ONT", 1), ("BC", 1)])
def test_get_links_by_province(province, expected_count):
    """Test get_links_by_province function."""
    with patch("legislative_crawler.config.crawl_config.get_links") as mock_get_links:
        mock_get_links.return_value = [
            {"identifier": "FED_CLC", "province": "FED"},
            {"identifier": "FED_CHRA", "province": "FED"},
            {"identifier": "ONT_ESA", "province": "ONT"},
            {"identifier": "BC_ESA", "province": "BC"}
        ]
        
        links = get_links_by_province(province)
        
        assert len(links) == expected_count
        for link in links:
            assert link["province"] == province
