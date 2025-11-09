import asyncio
import pytest
from src.crawlers.ontario_crawler import OntarioCrawler


@pytest.mark.asyncio
async def test_crawl_returns_expected_fields(monkeypatch):
    """Test that the OntarioCrawler returns all expected fields."""

    crawler = OntarioCrawler()

    # Mock AsyncWebCrawler output to avoid real web fetch
    class MockResult:
        success = True
        markdown = type("Markdown", (), {"fit_markdown": "Sample content"})
        error_message = None

    class MockCrawler:
        async def __aenter__(self): return self
        async def __aexit__(self, *args): pass
        async def arun(self, url, config): return MockResult()

    monkeypatch.setattr("src.crawlers.ontario_crawler.AsyncWebCrawler", lambda **_: MockCrawler())

    result = await crawler.crawl("https://example.com", "ON_FAKE", "Fake Law")

    # Verify output structure
    assert "identifier" in result
    assert "law_name" in result
    assert "url" in result
    assert "markdown" in result
    assert "embedding" in result
    assert isinstance(result["embedding"], list)
    assert result["identifier"] == "ON_FAKE"
    assert result["law_name"] == "Fake Law"
