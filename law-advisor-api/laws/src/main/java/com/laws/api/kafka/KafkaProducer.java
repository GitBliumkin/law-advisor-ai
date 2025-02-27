package com.laws.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.shared.basecrud.kafka.BaseKafkaProducer;

@Service
public class KafkaProducer implements BaseKafkaProducer{

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(String topic, String message) {
        logger.info("Sending Kafka message to topic '{}': {}", topic, message);
        kafkaTemplate.send(topic, message);
    }

}
