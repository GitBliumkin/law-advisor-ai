package com.companies.api.repositories;

import com.companies.api.tables.CompanyProfilesTable;
import com.shared.basecrud.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfilesRepository extends BaseRepository<CompanyProfilesTable, String> {}
