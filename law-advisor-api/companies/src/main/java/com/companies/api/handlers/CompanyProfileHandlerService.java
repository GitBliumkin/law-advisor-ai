package com.companies.api.handlers;

import org.springframework.stereotype.Service;

import com.companies.api.dtos.CompanyProfileDto;
import com.companies.api.repositories.CompanyProfileRepository;
import com.shared.basecrud.handlers.BaseHandlerService;

@Service
public class CompanyProfileHandlerService extends BaseHandlerService<CompanyProfileDto> {

    public CompanyProfileHandlerService(CompanyProfileRepository repository) {
        super(repository);
    }
}
