import asyncio
import json
from crawl4ai import AsyncWebCrawler, BrowserConfig, CrawlerRunConfig
from crawl4ai.markdown_generation_strategy import DefaultMarkdownGenerator
from crawl4ai.content_scraping_strategy import LXMLWebScrapingStrategy
from crawl4ai.content_filter_strategy import PruningContentFilter

async def scrape_markdown_to_json():
    # Headless browser with JS enabled
    browser_cfg = BrowserConfig(
        browser_type="chromium",
        headless=True,
        verbose=True,
        java_script_enabled=True
    )

    # Optional: Prune boilerplate using content filter
    prune_filter = PruningContentFilter(
        threshold=0.5,
        threshold_type="fixed",
        min_word_threshold=50
    )

    # Markdown generator with content filter and options
    md_generator = DefaultMarkdownGenerator(
        content_filter=prune_filter,
        options={
            "ignore_links": False,
            "body_width": 0
        }
    )

    run_cfg = CrawlerRunConfig(
        scraping_strategy=LXMLWebScrapingStrategy(),
        markdown_generator=md_generator,
        wait_until="networkidle",
        stream=False,
        verbose=True
    )

    async with AsyncWebCrawler(config=browser_cfg) as crawler:
        result = await crawler.arun("https://laws-lois.justice.gc.ca/eng/acts/L-2/page-1.html#h-339510", config=run_cfg)

        if result.success:
            md_obj = result.markdown  # MarkdownGenerationResult

            output = {
                "url": result.url,
                "raw_markdown": md_obj.raw_markdown,
                "fit_markdown": md_obj.fit_markdown,
                "references": md_obj.references_markdown,
                "with_citations": md_obj.markdown_with_citations
            }

            with open("markdown_output.json", "w", encoding="utf-8") as f:
                json.dump(output, f, ensure_ascii=False, indent=2)

            print("Markdown JSON saved to markdown_output.json")

        else:
            print("Crawl failed:", result.error_message)

if __name__ == "__main__":
    asyncio.run(scrape_markdown_to_json())
