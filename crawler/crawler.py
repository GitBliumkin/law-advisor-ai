"""Crawl ESA 2000 on CanLII and persist clean HTML snippets.

This crawler:
- Loads the CanLII ESA 2000 page (JS enabled)
- Extracts only `<div class="canliidocumentcontent">` to preserve all `p`/`div`
  elements and their classes for downstream metadata construction
- Writes both the full page (for debugging) and the content-only HTML fragment
  used downstream for chunking/embeddings
"""
from __future__ import annotations
import asyncio
from pathlib import Path
from typing import List, Optional
from crawl4ai import AsyncWebCrawler
from crawl4ai.async_configs import BrowserConfig, CrawlerRunConfig
from crawl4ai.content_scraping_strategy import LXMLWebScrapingStrategy
from bs4 import BeautifulSoup
from utils.logging_setup import logger
from config import START_URL, USER_AGENT, CONCURRENCY, RAW_DIR

CONTENT_SELECTOR = "div.canliidocumentcontent"

async def crawl(start_url: Optional[str] = None) -> List[Path]:
    """Crawl CanLII ESA 2000 page and save full and content-only HTML files."""
    url = start_url or START_URL
    logger.info(f"[CanLII] Starting crawl from {url}")
    
    # Headless browser with JS enabled
    browser_cfg = BrowserConfig(
        browser_type="chromium",
        headless=True,
        verbose=True,
        java_script_enabled=True
    )

    run_cfg = CrawlerRunConfig(
        scraping_strategy=LXMLWebScrapingStrategy(),
        wait_until="networkidle",
        stream=False,
        verbose=True
    )

    async with AsyncWebCrawler(config=browser_cfg) as crawler:
        result = await crawler.arun(url, config=run_cfg)

        if not result.success:
            logger.error(f"[CanLII] Crawl failed: {result.error_message}")
            return []

        saved_files: List[Path] = []

        # 1) Save entire page HTML for debugging/auditing
        full_html_path = RAW_DIR / "esa2000_canlii_full.html"
        full_html_path.write_text(result.html, encoding="utf-8")
        saved_files.append(full_html_path)
        logger.info(f"[CanLII] Saved full page HTML → {full_html_path}")

        # 2) Extract the exact content subtree (retain all tags/classes as-is)
        soup = BeautifulSoup(result.html, "lxml")
        content_div = soup.select_one(CONTENT_SELECTOR)

        if not content_div:
            logger.error(f"[CanLII] Selector '{CONTENT_SELECTOR}' not found on page.")
            return saved_files

        # Remove script/style under the content subtree only (keep structure intact)
        for tag in content_div.find_all(["script", "style"]):
            tag.decompose()

        content_html = str(content_div)
        content_path = RAW_DIR / "esa2000_canlii_content.html"
        content_path.write_text(content_html, encoding="utf-8")
        saved_files.append(content_path)
        logger.info(f"[CanLII] Saved content-only subtree → {content_path}")

    return saved_files

if __name__ == "__main__":
    asyncio.run(crawl()) 