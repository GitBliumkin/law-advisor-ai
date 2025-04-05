package com.laws.api.parser;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shared.models.dtos.ScrapedPageDto;

@Service
public class OntarioParserService {

    public void process(List<ScrapedPageDto> pages) {
        for (ScrapedPageDto page : pages) {
            System.out.println("Parsing Ontario law: " + page.getLawName());
            // parse page.pageContent and persist
        }
    }
}
