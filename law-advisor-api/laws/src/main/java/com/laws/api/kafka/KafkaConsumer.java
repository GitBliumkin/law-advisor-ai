package com.laws.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laws.api.parser.ParserRouterService;
import com.shared.basecrud.kafka.BaseKafkaConsumer;
import com.shared.models.dtos.ScraperDto;

@Service
public class KafkaConsumer implements BaseKafkaConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
	
	@Autowired
	private ParserRouterService routerService;
	
	@Override
	@KafkaListener(topics = "scraper_responses", groupId = "scraper-group")
	public void consumeMessage(String topic, @Payload String message) {
		 try {
	            ObjectMapper mapper = new ObjectMapper();
	            ScraperDto dto = mapper.readValue(message, ScraperDto.class);

	            if ("error".equalsIgnoreCase(dto.getStatus())) {
	            	throw new Error("Scraping error: " + dto.getMessage());
	            }

	            routerService.route(dto);

	        } catch (Exception e) {
	            // log parsing failure
	        	logger.error("Failed to deserialize or route message: " + e.getMessage());
	        }
		
	}

}
