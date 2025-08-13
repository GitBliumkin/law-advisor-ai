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
import os
import sys
from pathlib import Path
from typing import List, Optional
from crawl4ai import AsyncWebCrawler
from crawl4ai.async_configs import BrowserConfig, CrawlerRunConfig
from crawl4ai.content_scraping_strategy import LXMLWebScrapingStrategy
from playwright.async_api import async_playwright
from bs4 import BeautifulSoup
from utils.logging_setup import logger
from config import START_URL, RAW_DIR, DATA_DIR

CONTENT_SELECTOR = "div.canliidocumentcontent"

# Built-in user-agent (no .env required)
PRIMARY_USER_AGENT = (
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_6) "
    "AppleWebKit/537.36 (KHTML, like Gecko) "
    "Chrome/126.0.0.0 Safari/537.36"
)

CAPTCHA_MARKERS = [
    "captcha",
    "captcha-delivery.com",
    "Turnstile",
    "cf-chl",
]

def _is_captcha(html: str | None) -> bool:
    if not html:
        return True
    low = html.lower()
    return any(m.lower() in low for m in CAPTCHA_MARKERS)

async def _fetch_page(url: str, headless: bool, user_agent: str | None) -> str | None:
    """Fetch page HTML using a persistent Playwright context to preserve session cookies."""
    profile_dir = DATA_DIR / "browser" / "canlii_profile"
    profile_dir.mkdir(parents=True, exist_ok=True)
    async with async_playwright() as p:
        context = await p.chromium.launch_persistent_context(
            user_data_dir=str(profile_dir),
            headless=headless,
            user_agent=user_agent or PRIMARY_USER_AGENT,
            viewport={"width": 1400, "height": 900},
        )
        try:
            page = await context.new_page()
            await page.goto(url, wait_until="networkidle")
            html = await page.content()
            return html
        finally:
            await context.close()

async def _manual_verify_in_headful_browser(url: str, user_agent: str | None) -> str | None:
    """Open a persistent headful Playwright window using a fixed profile dir.
    Let the user solve captcha, then reload and return HTML while keeping cookies persisted.
    """
    if not (sys.stdin.isatty() and sys.stdout.isatty()):
        return None
    profile_dir = DATA_DIR / "browser" / "canlii_profile"
    profile_dir.mkdir(parents=True, exist_ok=True)
    async with async_playwright() as p:
        context = await p.chromium.launch_persistent_context(
            user_data_dir=str(profile_dir),
            headless=False,
            user_agent=user_agent or PRIMARY_USER_AGENT,
            viewport={"width": 1400, "height": 900},
        )
        try:
            page = await context.new_page()
            await page.goto(url)
            # Give the OS a moment to surface window
            await asyncio.sleep(0.5)
            try:
                input("[CanLII] Please solve verification in the visible browser window, then press Enter here to continue...")
            except EOFError:
                pass
            await page.reload(wait_until="networkidle")
            html = await page.content()
            return html
        finally:
            # Keep profile dir with cookies; close the context
            await context.close()

async def crawl(start_url: Optional[str] = None) -> List[Path]:
    """Crawl CanLII ESA 2000 page with a streamlined flow:
    1) One headless try
    2) One headful try
    3) Manual fallback in the same headful window
    Then save full and content-only HTML files.
    """
    url = start_url or START_URL
    logger.info(f"[CanLII] Starting crawl from {url}")
    
    # If we already have a recent content file, skip crawling
    saved_files: List[Path] = []
    content_path = RAW_DIR / "esa2000_canlii_content.html"
    full_html_path = RAW_DIR / "esa2000_canlii_full.html"
    if content_path.exists() and content_path.stat().st_size > 1024:
        logger.info(f"[CanLII] Existing content found → {content_path}, skipping crawl")
        return [content_path]

    # 1) Headless single try
    html: str | None = await _fetch_page(url, headless=True, user_agent=PRIMARY_USER_AGENT)

    # 2) Headful single try
    if _is_captcha(html):
        logger.warning("[CanLII] Headless blocked; retrying headful once...")
        html = await _fetch_page(url, headless=False, user_agent=PRIMARY_USER_AGENT)

    # Manual fallback: ask user to solve inside the SAME headful crawler window
    if _is_captcha(html):
        logger.warning("[CanLII] Manual verification required. Using the same headful browser window for human verification...")
        html = await _manual_verify_in_headful_browser(url, PRIMARY_USER_AGENT)

    # If still blocked, save what we have for debugging and exit
    if _is_captcha(html):
        logger.error("[CanLII] Bot-protection still present after retries. Consider proxies or cookie reuse.")
        full_html_path.write_text(html or "", encoding="utf-8")
        return [full_html_path]

    # 1) Save entire page HTML for debugging/auditing
    full_html_path.write_text(html, encoding="utf-8")
    saved_files.append(full_html_path)
    logger.info(f"[CanLII] Saved full page HTML → {full_html_path}")

    # 2) Extract the exact content subtree (retain all tags/classes as-is)
    soup = BeautifulSoup(html, "lxml")
    content_div = soup.select_one(CONTENT_SELECTOR)

    if not content_div:
        logger.error(f"[CanLII] Selector '{CONTENT_SELECTOR}' not found on page.")
        return saved_files

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