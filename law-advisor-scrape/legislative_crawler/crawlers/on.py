"""
Ontario law crawler module.
"""
import asyncio
from typing import Dict, Any, List, Optional

from crawl4ai import AsyncWebCrawler, CrawlerRunConfig, CacheMode
from crawl4ai.content_filter_strategy import PruningContentFilter
from crawl4ai.extraction_strategy import JsonCssExtractionStrategy

from ..utils.helpers import logger, format_result, handle_error
from ..config.crawl_config import CRAWLER_SETTINGS

class OntarioCrawler:
    """
    Crawler for Ontario laws.
    """
    
    def __init__(self, settings: Optional[Dict[str, Any]] = None):
        """
        Initialize the Ontario crawler.
        
        Args:
            settings (Optional[Dict[str, Any]]): Crawler settings
        """
        self.settings = settings or CRAWLER_SETTINGS
        logger.info("Ontario crawler initialized")
    
    async def crawl_law(self, identifier: str, url: str, law_name: str) -> Dict[str, Any]:
        """
        Crawl a specific Ontario law.
        
        Args:
            identifier (str): Law identifier
            url (str): Law URL
            law_name (str): Law name
            
        Returns:
            Dict[str, Any]: Crawling result
        """
        try:
            logger.info(f"Crawling {identifier} ({url})")
            
            # Configure the crawler for Ontario law websites
            config = CrawlerRunConfig(
                # Content selection parameters
                css_selector="main.ontario-main-content", # Target main content area
                excluded_tags=["nav", "footer", "header", "script", "style"],
                word_count_threshold=10,  # Skip very short text blocks
                exclude_external_links=True,
                
                # Content filtering
                content_filter=PruningContentFilter(
                    threshold=0.3,  # Lower threshold to keep more content
                    threshold_type="fixed",
                    min_word_threshold=20
                ),
                
                # Enable markdown generation with fit_markdown
                fit_markdown=True,
                
                # Cache settings
                cache_mode=CacheMode.MEMORY
            )
            
            # Create schema for structured extraction if needed
            law_schema = {
                "name": "Law Content",
                "baseSelector": "main.ontario-main-content",
                "fields": [
                    {"name": "title", "selector": "h1", "type": "text"},
                    {"name": "sections", "selector": "section", "type": "text"},
                    {"name": "chapters", "selector": ".chapter", "type": "text"},
                    {"name": "parts", "selector": ".part", "type": "text"}
                ]
            }
            
            # Create the crawler and run it
            async with AsyncWebCrawler() as crawler:
                result = await crawler.arun(url=url, config=config)
                
                if not result.success:
                    logger.error(f"Failed to crawl {url}: {result.error_message}")
                    return handle_error(identifier, url, law_name, 
                                       Exception(f"Crawl failed: {result.error_message}"))
                
                # Use the filtered markdown content
                markdown_content = result.fit_markdown if result.fit_markdown else result.markdown
                
                # If markdown is empty, try to use the raw HTML
                if not markdown_content and result.cleaned_html:
                    logger.warning(f"No markdown generated for {url}, using cleaned HTML")
                    # You might want to implement a fallback HTML-to-markdown conversion here
                
                # Try structured extraction as a fallback or enhancement
                if not markdown_content or len(markdown_content) < 100:
                    logger.warning(f"Minimal content extracted, trying structured extraction for {url}")
                    # Run again with structured extraction
                    extraction_config = CrawlerRunConfig(
                        css_selector="main.ontario-main-content",
                        excluded_tags=["nav", "footer", "header", "script", "style"],
                        extraction_strategy=JsonCssExtractionStrategy(law_schema),
                        cache_mode=CacheMode.MEMORY
                    )
                    
                    structured_result = await crawler.arun(url=url, config=extraction_config)
                    if structured_result.success and structured_result.extracted_content:
                        import json
                        try:
                            structured_data = json.loads(structured_result.extracted_content)
                            # Convert structured data to markdown
                            markdown_content = f"# {structured_data.get('title', law_name)}\n\n"
                            
                            for section in structured_data.get('sections', []):
                                markdown_content += f"{section}\n\n"
                            
                            for chapter in structured_data.get('chapters', []):
                                markdown_content += f"{chapter}\n\n"
                                
                            for part in structured_data.get('parts', []):
                                markdown_content += f"{part}\n\n"
                        except json.JSONDecodeError:
                            logger.error(f"Failed to parse structured data for {url}")
            
            return format_result(
                identifier=identifier,
                url=url,
                law_name=law_name,
                markdown_content=markdown_content
            )
        except Exception as e:
            return handle_error(identifier, url, law_name, e)
    
    async def crawl_all_laws(self, laws: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Crawl all Ontario laws in parallel.
        
        Args:
            laws (List[Dict[str, Any]]): List of laws to crawl
            
        Returns:
            List[Dict[str, Any]]: List of crawling results
        """
        tasks = []
        for law in laws:
            task = self.crawl_law(
                identifier=law["identifier"],
                url=law["url"],
                law_name=law["law_name"]
            )
            tasks.append(task)
        
        return await asyncio.gather(*tasks)
