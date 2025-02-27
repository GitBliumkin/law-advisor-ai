package com.laws.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.shared.basecrud.kafka.BaseKafkaConsumer;

@Service
public class KafkaConsumer implements BaseKafkaConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	
	@Override
	@KafkaListener(topics = "scraper_responses", groupId = "scraper-group")
	public void consumeMessage(String topic, String message) {
		logger.info("Received Kafka message from '{}': {}", topic, message);
		
	}

}
