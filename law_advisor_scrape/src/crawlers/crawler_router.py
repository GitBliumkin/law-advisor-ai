import json
from pathlib import Path
from typing import List, Dict, Any

from legislative_crawler.utils.helpers import logger
from legislative_crawler.crawlers.ontario_crawler import OntarioCrawler

LEGISLATION_JSON_PATH = Path(__file__).parent.parent / "data/legislation_index.json"

class CrawlerRouter:
    """Handles crawler selection, law loading, and single-law crawling."""

    @staticmethod
    def get_crawler(province: str):
        province = province.lower()
        if province == "ontario":
            return OntarioCrawler()
        # Add more provinces here
        else:
            raise ValueError(f"No crawler implemented for province: {province}")

    @staticmethod
    def load_laws_for_province(province: str) -> List[Dict[str, Any]]:
        try:
            with open(LEGISLATION_JSON_PATH, "r", encoding="utf-8") as f:
                all_laws = json.load(f)

            entry = next(
                (item for item in all_laws if item["province"].lower() == province.lower()),
                None
            )

            if not entry or "pages" not in entry:
                raise ValueError(f"No laws found for province: {province}")

            return entry["pages"]

        except Exception as e:
            logger.error(f"Error loading law data: {e}")
            raise

    @staticmethod
    async def crawl_law(crawler, law: Dict[str, Any]) -> Dict[str, Any]:
        try:
            crawl_result = await crawler.crawl(law["url"])
            return {
                **law,
                "status": "success",
                "markdown": crawl_result
            }
        except Exception as e:
            logger.warning(f"Failed to crawl {law['identifier']}: {e}")
            return {
                **law,
                "status": "error",
                "error": str(e)
            }
