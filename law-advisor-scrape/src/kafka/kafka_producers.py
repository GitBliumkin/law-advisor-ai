# kafka_producer.py
import json
from typing import Dict, Any
from aiokafka import AIOKafkaProducer

from your_project.config.crawl_config import KAFKA_SETTINGS
from your_project.utils.helpers import logger

class KafkaProducerService:
    def __init__(self, bootstrap_servers: str):
        self.bootstrap_servers = bootstrap_servers
        self._producer = None

    async def start(self):
        self._producer = AIOKafkaProducer(bootstrap_servers=self.bootstrap_servers)
        await self._producer.start()
        logger.info("Kafka producer started")

    async def stop(self):
        if self._producer:
            await self._producer.stop()
            logger.info("Kafka producer stopped")

    async def send(self, topic: str, data: Dict[str, Any]):
        if not self._producer:
            raise RuntimeError("Kafka producer not initialized")
        try:
            await self._producer.send_and_wait(topic, json.dumps(data).encode("utf-8"))
            logger.info(f"Sent to Kafka topic {topic}")
        except Exception as e:
            logger.error(f"Failed to send to Kafka: {e}")
