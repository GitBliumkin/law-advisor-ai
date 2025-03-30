# legislative_crawler/kafka/consumer.py
import json
from aiokafka import AIOKafkaConsumer
from legislative_crawler.config.crawl_config import KAFKA_SETTINGS
from legislative_crawler.utils.helpers import logger

class KafkaConsumerService:
    def __init__(self, topic: str, group_id: str):
        self.topic = topic
        self.group_id = group_id
        self.consumer = None

    async def start(self):
        self.consumer = AIOKafkaConsumer(
            self.topic,
            bootstrap_servers=KAFKA_SETTINGS["bootstrap_servers"],
            group_id=self.group_id,
            auto_offset_reset="earliest"
        )
        await self.consumer.start()
        logger.info(f"Kafka consumer started for topic: {self.topic}")

    async def consume_one(self):
        try:
            async for message in self.consumer:
                value = message.value.decode("utf-8")
                logger.info(f"Received message: {value}")
                return value  # return the first message and exit
        finally:
            await self.consumer.stop()
            logger.info("Kafka consumer stopped")
