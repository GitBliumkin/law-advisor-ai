# src/crawlers/ontario_crawler.py

import asyncio
import json
from datetime import datetime
from typing import Dict, Any, List

from crawl4ai import AsyncWebCrawler, BrowserConfig, CrawlerRunConfig
from crawl4ai.markdown_generation_strategy import DefaultMarkdownGenerator
from crawl4ai.content_scraping_strategy import LXMLWebScrapingStrategy
from crawl4ai.content_filter_strategy import PruningContentFilter

from src.embedder.embedding import embed_text


class OntarioCrawler:
    def __init__(self):
        # Headless browser with JS enabled
        self.browser_config = BrowserConfig(
            browser_type="chromium",
            headless=True,
            verbose=True,
            java_script_enabled=True,
        )

        # Optional: prune boilerplate using content filter
        self.prune_filter = PruningContentFilter(
            threshold=0.5,
            threshold_type="fixed",
            min_word_threshold=50,
        )

        # Markdown generator with content filter and options
        self.markdown_generator = DefaultMarkdownGenerator(
            content_filter=self.prune_filter,
            options={
                # set to True/False depending on whether you want link URLs in markdown
                "ignore_links": True,
                "body_width": 0,
            },
        )

        self.run_config = CrawlerRunConfig(
            scraping_strategy=LXMLWebScrapingStrategy(),
            markdown_generator=self.markdown_generator,
            wait_until="networkidle",
            stream=False,
            verbose=True,
        )

    # -------- core crawl --------

    async def crawl(self, url: str, identifier: str, law_name: str) -> Dict[str, Any]:
        """
        Crawl a single Ontario law page and return markdown + embedding.
        """
        async with AsyncWebCrawler(config=self.browser_config) as crawler:
            result = await crawler.arun(url, config=self.run_config)

            if not result.success:
                raise RuntimeError(f"Crawl failed for {url}: {result.error_message}")

            md = result.markdown  # MarkdownGenerationResult
            page_markdown = md.fit_markdown

            # compute embedding for full page markdown
            embedding = embed_text(page_markdown)

            return {
                "identifier": identifier,
                "law_name": law_name,
                "url": url,
                "markdown": page_markdown,
                "embedding": embedding,
                "scrapeTime": datetime.utcnow().isoformat(),
            }

    # -------- wrappers for CLI compatibility --------

    async def crawl_law(self, identifier: str, url: str, law_name: str) -> Dict[str, Any]:
        """
        Method used by CLI: crawl a single law by identifier/url/law_name.
        """
        return await self.crawl(url=url, identifier=identifier, law_name=law_name)

    async def crawl_all_laws(self, laws: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Method used by CLI: crawl a list of law definitions.
        Each `law` entry is expected to have "identifier", "url", "law_name".
        """
        tasks = [
            self.crawl_law(
                identifier=law["identifier"],
                url=law["url"],
                law_name=law["law_name"],
            )
            for law in laws
        ]
        return await asyncio.gather(*tasks)

    # -------- optional helper for quick manual testing --------

    async def crawl_and_save(
        self,
        url: str,
        identifier: str,
        law_name: str,
        output_path: str = "markdown_output.json",
    ) -> None:
        """
        Crawl a single page and save the result to a JSON file.
        Useful for debugging without the CLI.
        """
        result = await self.crawl(url=url, identifier=identifier, law_name=law_name)

        with open(output_path, "w", encoding="utf-8") as f:
            json.dump(result, f, ensure_ascii=False, indent=2)

        print(f"Markdown JSON saved to {output_path}")


# Run this file directly for a quick smoke test:
if __name__ == "__main__":
    async def _test():
        crawler = OntarioCrawler()
        url = "https://www.ontario.ca/laws/statute/00e41"
        identifier = "ON_EPA"
        law_name = "Environmental Protection Act"
        
        print("Starting crawl...")
        result = await crawler.crawl(url=url, identifier=identifier, law_name=law_name)

        print("\n✅ Crawl complete!\n")
        print("Keys:", result.keys())
        print("identifier:", result["identifier"])
        print("law_name:", result["law_name"])
        print("markdown length:", len(result["markdown"]))
        print("embedding length:", len(result["embedding"]))
        print("embedding sample:", result["embedding"][:5])

    asyncio.run(_test())
