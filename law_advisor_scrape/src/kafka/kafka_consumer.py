import json
from aiokafka import AIOKafkaConsumer

from src.config.crawl_config import KAFKA_SETTINGS
from src.utils.helpers import logger


class KafkaConsumerService:
    @staticmethod
    def safe_deserializer(m):
        try:
            return json.loads(m.decode("utf-8"))
        except Exception:
            return m.decode("utf-8")

    def __init__(self, topic: str, group_id: str):
        self.topic = topic
        self.group_id = group_id
        self.consumer = AIOKafkaConsumer(
            self.topic,
            bootstrap_servers=KAFKA_SETTINGS["bootstrap_servers"],
            group_id=self.group_id,
            auto_offset_reset="earliest",
            enable_auto_commit=True,
            value_deserializer=KafkaConsumerService.safe_deserializer
        )

    async def start(self):
        await self.consumer.start()
        logger.info(f"Kafka consumer started for topic: {self.topic}")

    async def stop(self):
        await self.consumer.stop()
        logger.info("Kafka consumer stopped")

    async def consume_one(self):
        try:
            async for message in self.consumer:
                logger.info(f"Received message: {message.value}")
                return message.value
        except Exception as e:
            logger.error(f"Error consuming message: {e}")
        finally:
            await self.stop()
