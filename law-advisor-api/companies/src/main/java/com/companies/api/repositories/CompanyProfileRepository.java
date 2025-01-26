package com.companies.api.repositories;

import com.companies.api.tables.CompanyProfileTable;
import com.shared.basecrud.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends BaseRepository<CompanyProfileTable> {}
