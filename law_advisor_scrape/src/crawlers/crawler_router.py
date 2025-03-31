import json
from pathlib import Path
from typing import List, Dict, Any

from src.utils.helpers import logger
from src.crawlers.ontario_crawler import OntarioCrawler

LEGISLATION_JSON_PATH = Path(__file__).parent.parent / "resources/links.json"

class CrawlerRouter:
    """Handles crawler selection, law loading, and single-law crawling."""

    PROVINCE_ALIAS = {
        "ontario": "ON",
        "federal": "FED",
        "british columbia": "BC",
    }

    @staticmethod
    def normalize_province(province: str) -> str:
        return CrawlerRouter.PROVINCE_ALIAS.get(province.lower(), province.upper())

    @staticmethod
    def get_crawler(province: str):
        province = CrawlerRouter.normalize_province(province)
        if province == "ON":
            return OntarioCrawler()
        # Add more provinces here
        else:
            raise ValueError(f"No crawler implemented for province: {province}")

    @staticmethod
    def load_laws_for_province(province: str) -> List[Dict[str, Any]]:
        province = CrawlerRouter.normalize_province(province)

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
