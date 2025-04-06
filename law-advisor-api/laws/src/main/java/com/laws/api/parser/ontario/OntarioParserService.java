package com.laws.api.parser.ontario;

import java.util.List;

import org.springframework.stereotype.Service;

import com.laws.api.utils.Utils;
import com.shared.models.dtos.ScrapedPageDto;

@Service
public class OntarioParserService {

    public void process(List<ScrapedPageDto> pages) {
        for (ScrapedPageDto page : pages) {
        	String cleanedPageContent = Utils.cleanBlock(page.getPageContent());
            page.setPageContent(cleanedPageContent);
        }
    }
}
