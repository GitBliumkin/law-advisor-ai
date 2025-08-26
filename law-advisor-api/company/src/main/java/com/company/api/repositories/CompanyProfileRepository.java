package com.company.api.repositories;

import com.company.api.tables.CompanyProfileTable;
import com.shared.basecrud.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends BaseRepository<CompanyProfileTable, String> {}
