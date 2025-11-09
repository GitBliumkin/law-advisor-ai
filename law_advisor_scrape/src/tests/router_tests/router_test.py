import asyncio
import pytest
from src.crawlers.crawler_router import CrawlerRouter


@pytest.mark.asyncio
async def test_router_crawl_law(monkeypatch):
    """Test that router correctly delegates crawl to the crawler."""

    class MockCrawler:
        async def crawl(self, url, identifier, law_name):
            return {"identifier": identifier, "law_name": law_name, "url": url, "markdown": "content", "embedding": [0.1]}

    law = {"url": "https://example.com", "identifier": "ON_FAKE", "law_name": "Fake Law"}
    result = await CrawlerRouter.crawl_law(MockCrawler(), law)

    assert result["identifier"] == "ON_FAKE"
    assert "embedding" in result
    assert result["law_name"] == "Fake Law"


def test_load_laws_for_province(monkeypatch, tmp_path):
    """Test that router loads JSON and filters province laws correctly."""
    fake_json = [{"province": "ON", "pages": [{"identifier": "ON_1", "law_name": "Test", "url": "https://example.com"}]}]
    file_path = tmp_path / "links.json"
    file_path.write_text('[{"province": "ON", "pages": [{"identifier": "ON_1", "law_name": "Test", "url": "https://x"}]}]', encoding="utf-8")

    monkeypatch.setattr("src.crawlers.crawler_router.LEGISLATION_JSON_PATH", file_path)

    laws = CrawlerRouter.load_laws_for_province("ON", "ignored")
    assert len(laws) == 1
    assert laws[0]["identifier"] == "ON_1"
