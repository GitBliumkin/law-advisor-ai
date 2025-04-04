import json
from typing import Dict, Any, Optional
from aiokafka import AIOKafkaProducer

from src.config.crawl_config import KAFKA_SETTINGS
from src.utils.helpers import logger


class KafkaProducerService:
    def __init__(self, bootstrap_servers: Optional[str] = None):
        self.bootstrap_servers = bootstrap_servers or KAFKA_SETTINGS["bootstrap_servers"]
        self._producer = AIOKafkaProducer(
            bootstrap_servers=self.bootstrap_servers,
            value_serializer=lambda m: json.dumps(m).encode("utf-8")
        )

    async def start(self):
        await self._producer.start()
        logger.info("Kafka producer started")

    async def stop(self):
        await self._producer.stop()
        logger.info("Kafka producer stopped")

    async def send(self, topic: str, data: Dict[str, Any]):
        try:
            await self._producer.send_and_wait(topic, data)
            logger.info(f"Sent message to topic {topic}: {data}")
        except Exception as e:
            logger.error(f"Failed to send message to topic {topic}: {e}")
