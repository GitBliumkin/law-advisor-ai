import asyncio
import json
from crawl4ai import AsyncWebCrawler, BrowserConfig, CrawlerRunConfig
from crawl4ai.markdown_generation_strategy import DefaultMarkdownGenerator
from crawl4ai.content_scraping_strategy import LXMLWebScrapingStrategy
from crawl4ai.content_filter_strategy import PruningContentFilter


class OntarioCrawler:
    def __init__(self):
        # Headless browser with JS enabled
        self.browser_config = BrowserConfig(
            browser_type="chromium",
            headless=True,
            verbose=True,
            java_script_enabled=True
        )

        # Optional: Prune boilerplate using content filter
        self.prune_filter = PruningContentFilter(
            threshold=0.5,
            threshold_type="fixed",
            min_word_threshold=50
        )

        # Markdown generator with content filter and options
        self.markdown_generator = DefaultMarkdownGenerator(
            content_filter=self.prune_filter,
            options={
                "ignore_links": False,
                "body_width": 0
            }
        )

        self.run_config = CrawlerRunConfig(
            scraping_strategy=LXMLWebScrapingStrategy(),
            markdown_generator=self.markdown_generator,
            wait_until="networkidle",
            stream=False,
            verbose=True
        )

    async def crawl(self, url: str) -> dict:
        """
        Run the crawler and return markdown output as a dictionary.
        """
        async with AsyncWebCrawler(config=self.browser_config) as crawler:
            result = await crawler.arun(url, config=self.run_config)

            if result.success:
                md_obj = result.markdown  # MarkdownGenerationResult

                output = {
                    "url": result.url,
                    "raw_markdown": md_obj.raw_markdown,
                    "fit_markdown": md_obj.fit_markdown,
                    "references": md_obj.references_markdown,
                    "with_citations": md_obj.markdown_with_citations
                }

                return output
            else:
                raise RuntimeError(f"Crawl failed: {result.error_message}")

    async def crawl_and_save(self, url: str, output_path: str = "markdown_output.json"):
        """
        Run the crawler and save markdown output to a JSON file.
        """
        try:
            output = await self.crawl(url)

            with open(output_path, "w", encoding="utf-8") as f:
                json.dump(output, f, ensure_ascii=False, indent=2)

            print(f"Markdown JSON saved to {output_path}")
        except Exception as e:
            print(str(e))