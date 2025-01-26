package com.companies.api.repositories;

import org.springframework.stereotype.Repository;

import com.companies.api.dtos.CompanyProfileDto;
import com.shared.basecrud.repositories.BaseRepository;

@Repository
public class CompanyProfileRepository extends BaseRepository<CompanyProfileDto> {

    public CompanyProfileRepository() {
        super(CompanyProfileDto.class);
    }
}
