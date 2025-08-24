package com.qdrant.api.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qdrant.api.parser.ontario.OntarioParserService;
import com.shared.models.dtos.ScraperDto;
import com.shared.models.enums.SupportedRegions;

@Service
public class ParserRouterService {
	
	@Autowired
	private OntarioParserService ontarioParser;


	    public void route(ScraperDto scraperDto) {
	    	SupportedRegions region = scraperDto.getRegion();

	        switch (region) {
	            case Ontario:
	                ontarioParser.process(scraperDto.getPages());
	                break;
	            default:
	                throw new UnsupportedOperationException("Unknown regopm: " + region);
	        }
	    }

}
