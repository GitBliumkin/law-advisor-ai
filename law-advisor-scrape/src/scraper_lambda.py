#!/usr/bin/env python3
"""
Legislative Crawler - Kafka Lambda Entrypoint

Listens for province names from Kafka (`scraper-requests` topic), runs the appropriate crawler,
and sends the output to Kafka (`scraper-responses` topic).
"""
import os
import sys
import asyncio
from pathlib import Path

# Add the parent directory to sys.path
sys.path.insert(0, str(Path(__file__).parent.parent))

from legislative_crawler.kafka.kafka_consumer import KafkaConsumerService
from legislative_crawler.kafka.kafka_producer import KafkaProducerService
from legislative_crawler.config.crawl_config import KAFKA_SETTINGS
from legislative_crawler.utils.helpers import logger

from legislative_crawler.crawlers.ontario_crawler import OntarioCrawler
# Add other crawlers here as you implement them

class CrawlerRouter:
    """Routes province names to their respective crawler classes."""

    @staticmethod
    def get_crawler(province: str):
        province = province.lower()
        if province == "ontario":
            return OntarioCrawler()
        # Add more provinces here
        else:
            raise ValueError(f"No crawler implemented for province: {province}")

async def handle_province_crawl(province: str) -> dict:
    """Crawl based on the province and return the result as JSON."""
    try:
        crawler = CrawlerRouter.get_crawler(province)
        url = "https://laws-lois.justice.gc.ca/eng/acts/L-2/page-1.html#h-339510"  # You can make this dynamic
        result = await crawler.crawl(url)
        return {
            "province": province,
            "status": "success",
            "result": result
        }
    except Exception as e:
        logger.error(f"Crawling failed: {e}")
        return {
            "province": province,
            "status": "error",
            "message": str(e)
        }

async def kafka_listen_and_process():
    consumer = KafkaConsumerService(topic="scraper-requests", group_id="scraper-group")
    producer = KafkaProducerService(KAFKA_SETTINGS["bootstrap_servers"])

    await consumer.start()
    await producer.start()

    try:
        logger.info("Kafka Lambda is now listening...")
        while True:
            province = await consumer.consume_one()
            logger.info(f"Received crawl request for province: {province}")

            result_json = await handle_province_crawl(province)

            await producer.send("scraper-responses", result_json)
            logger.info(f"Result for {province} sent to scraper-responses")
    finally:
        await consumer.consumer.stop()
        await producer.stop()

if __name__ == "__main__":
    asyncio.run(kafka_listen_and_process())
