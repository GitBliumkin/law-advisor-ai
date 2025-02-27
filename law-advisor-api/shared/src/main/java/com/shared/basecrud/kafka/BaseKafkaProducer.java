package com.shared.basecrud.kafka;

public interface BaseKafkaProducer {
	void sendMessage(String topic, String message);
}
