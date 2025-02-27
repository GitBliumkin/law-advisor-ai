package com.shared.basecrud.kafka;

public interface BaseKafkaConsumer {
	void consumeMessage(String topic, String message);
}
