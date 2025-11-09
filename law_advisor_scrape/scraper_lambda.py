#!/usr/bin/env python3
"""
Legislative Crawler - Kafka Lambda Entrypoint

Listens for crawl requests from Kafka (`scraper-requests` topic), runs the appropriate
crawler via CrawlerRouter, and sends the output to Kafka (`scraper-responses` topic).

Expected Kafka request message shape (JSON):
{
    "province": "ON",          # province code (e.g., ON, FED, BC)
    "identifier": "batch-123"  # arbitrary identifier for tracking (kept & echoed back)
}
"""

import os
import sys
import asyncio
from pathlib import Path

# Ensure `src` is importable when this file is executed directly
CURRENT_DIR = os.path.dirname(__file__)
SRC_PATH = os.path.join(CURRENT_DIR, "src")
if SRC_PATH not in sys.path:
    sys.path.append(SRC_PATH)

from src.kafka.kafka_consumer import KafkaConsumerService
from src.kafka.kafka_producer import KafkaProducerService
from src.config.crawl_config import KAFKA_SETTINGS
from src.utils.helpers import logger
from src.crawlers.crawler_router import CrawlerRouter


class ScraperLambda:
    """Main crawling handler using routing logic from CrawlerRouter."""

    @classmethod
    async def handle_province_crawl(cls, province: str, identifier: str) -> dict:
        """
        Crawl all laws for a province and return results.

        Args:
            province: Province code from the message (e.g., "ON", "FED", "BC").
            identifier: An identifier from the message (kept for tracking, not used to filter).

        Returns:
            dict: Result payload with province, status, and pages (or error info).
        """
        try:
            normalized_province = province.upper().strip()
            logger.info(
                f"Starting crawl for province: {normalized_province} "
                f"(request identifier: {identifier})"
            )

            # Get appropriate crawler for the province
            crawler = CrawlerRouter.get_crawler(normalized_province)

            # Load laws for the province from links.json
            # Note: CrawlerRouter.load_laws_for_province currently expects (province, identifire)
            law_pages = CrawlerRouter.load_laws_for_province(
                normalized_province,
                identifier  # passed through to match current signature, even if unused
            )

            results = []
            for law in law_pages:
                # law is expected to have at least: "url", "identifier", "law_name"
                result = await CrawlerRouter.crawl_law(crawler, law)
                results.append(result)

            logger.info(
                f"Successfully crawled {len(results)} laws for province: {normalized_province}"
            )

            return {
                "province": normalized_province,
                "request_identifier": identifier,
                "status": "success",
                "pages": results,
            }

        except Exception as e:
            logger.error(f"Crawling failed for province {province}: {e}")
            return {
                "province": province,
                "request_identifier": identifier,
                "status": "error",
                "message": str(e),
            }


async def kafka_listen_and_process():
    """
    Main loop: listen to Kafka for crawl requests, process them, and send responses.

    - Consumes messages from KAFKA_SETTINGS["request_topic"]
    - Expects messages with at least "province" and "identifier" keys
    - Runs ScraperLambda.handle_province_crawl(...)
    - Produces results to KAFKA_SETTINGS["response_topic"]
    """
    consumer = KafkaConsumerService(
        topic=KAFKA_SETTINGS["request_topic"],
        group_id=KAFKA_SETTINGS["group_id"],
    )
    producer = KafkaProducerService(KAFKA_SETTINGS["bootstrap_servers"])

    await consumer.start()
    await producer.start()

    try:
        logger.info(
            f"Kafka Lambda is now listening on topic: {KAFKA_SETTINGS['request_topic']}"
        )
        while True:
            message = await consumer.consume_one()

            if not message:
                logger.warning("Skipping null or invalid Kafka message")
                continue

            logger.info(f"Raw Kafka message received: {message}")

            province = message.get("province")
            identifier = message.get("identifier")

            # Basic validation
            if not isinstance(province, str) or not province.strip():
                logger.warning(f"Skipping invalid or empty province value: {province}")
                continue

            if not isinstance(identifier, str) or not identifier.strip():
                logger.warning(f"Skipping invalid or empty identifier value: {identifier}")
                continue

            logger.info(
                f"Received crawl request for province: {province}, "
                f"identifier: {identifier}"
            )

            # Perform province-level crawl (all laws for that province)
            result_json = await ScraperLambda.handle_province_crawl(
                province=province,
                identifier=identifier,
            )

            # Send result back to Kafka
            await producer.send(KAFKA_SETTINGS["response_topic"], result_json)
            logger.info(
                f"Result for province {province} (identifier: {identifier}) "
                f"sent to topic: {KAFKA_SETTINGS['response_topic']}"
            )
    finally:
        await consumer.stop()
        await producer.stop()
        logger.info("Kafka Lambda shut down")


if __name__ == "__main__":
    asyncio.run(kafka_listen_and_process())
