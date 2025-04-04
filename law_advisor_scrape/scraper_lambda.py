#!/usr/bin/env python3
"""
Legislative Crawler - Kafka Lambda Entrypoint

Listens for province names from Kafka (`scraper-requests` topic), runs the appropriate crawler,
and sends the output to Kafka (`scraper-responses` topic).
"""
import os
import sys
import asyncio
import aiokafka
from pathlib import Path

# Add the parent directory to sys.path
sys.path.append(os.path.join(os.path.dirname(__file__), "src"))

from src.kafka.kafka_consumer import KafkaConsumerService
from src.kafka.kafka_producer import KafkaProducerService
from src.config.crawl_config import KAFKA_SETTINGS
from src.utils.helpers import logger
from src.crawlers.crawler_router import CrawlerRouter


class ScraperLambda:
    """Main crawling handler using routing logic from CrawlerRouter."""

    @classmethod
    async def handle_province_crawl(cls, province: str) -> dict:
        """
        Crawl all laws for a province and return results.
        """
        try:
            logger.info(f"Starting crawl for province: {province}")
            crawler = CrawlerRouter.get_crawler(province.lower())
            law_pages = CrawlerRouter.load_laws_for_province(province)

            results = []
            for law in law_pages:
                result = await CrawlerRouter.crawl_law(crawler, law)
                results.append(result)

            return {
                "province": province,
                "status": "success",
                "pages": results
            }

        except Exception as e:
            logger.error(f"Crawling failed for {province}: {e}")
            return {
                "province": province,
                "status": "error",
                "message": str(e)
            }


async def kafka_listen_and_process():
    """
    Start Kafka consumer to receive province names,
    run crawlers, and send results back via Kafka producer.
    """
    consumer = KafkaConsumerService(topic=KAFKA_SETTINGS["request_topic"],group_id=KAFKA_SETTINGS["group_id"])

    producer = KafkaProducerService(KAFKA_SETTINGS["bootstrap_servers"])

    await consumer.start()
    await producer.start()

    try:
        logger.info("Kafka Lambda is now listening...")
        while True:
            province = await consumer.consume_one()
            if not isinstance(province, str) or not province.strip():
                logger.warning(f"Skipping invalid or empty message: {province}")
                return 

            logger.info(f"Received crawl request for province: {province}")

            result_json = await ScraperLambda.handle_province_crawl(province)
            await producer.send(KAFKA_SETTINGS["response_topic"], result_json)
            logger.info(f"Result for {province} sent to scraper-responses")
    finally:
        await consumer.consumer.stop()
        await producer.stop()


if __name__ == "__main__":
    asyncio.run(kafka_listen_and_process())
